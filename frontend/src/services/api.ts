import axios, { AxiosError, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';
import type { AuthResponse } from '../types/auth';
import { tokenStorage } from './tokenStorage';

type RetriableRequestConfig = InternalAxiosRequestConfig & {
  _retry?: boolean;
};

type FlexibleAuthResponse = AuthResponse & {
  token?: string;
  accessToken?: string;
  access_token?: string;
  refreshToken?: string;
  refresh_token?: string;
  tokenType?: string;
  token_type?: string;
};

const rawBaseUrl =
  import.meta.env.VITE_API_BASE_URL ?? import.meta.env.VITE_API_URL ?? 'http://127.0.0.1:8080/api';

const normalizedBaseUrl = rawBaseUrl.replace(/\/$/, '');

export const API_BASE_URL = normalizedBaseUrl.endsWith('/api')
  ? normalizedBaseUrl
  : `${normalizedBaseUrl}/api`;

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    Accept: 'application/json',
  },
});

let refreshPromise: Promise<FlexibleAuthResponse> | null = null;

function getAccessTokenFromAuthResponse(auth: FlexibleAuthResponse) {
  return auth.token || auth.accessToken || auth.access_token || '';
}

function getRefreshTokenFromAuthResponse(auth: FlexibleAuthResponse) {
  return auth.refreshToken || auth.refresh_token || '';
}

function saveAuthResponse(auth: FlexibleAuthResponse) {
  const accessToken = getAccessTokenFromAuthResponse(auth);
  const refreshToken = getRefreshTokenFromAuthResponse(auth);
  const tokenType = auth.tokenType || auth.token_type || 'Bearer';

  if (accessToken) {
    localStorage.setItem('travi_access_token', accessToken);
  }

  if (refreshToken) {
    localStorage.setItem('travi_refresh_token', refreshToken);
  }

  localStorage.setItem('travi_token_type', tokenType);
}

const normalizeRequestUrl = (url?: string) => {
  if (!url) {
    return '';
  }

  return url.startsWith('http') ? url.replace(API_BASE_URL, '') : url;
};

const isPublicAuthRequest = (url?: string) => {
  const normalizedUrl = normalizeRequestUrl(url);

  return normalizedUrl.startsWith('/v1/auth/') && normalizedUrl !== '/v1/auth/logout';
};

const isPublicRequest = (url?: string) => {
  const normalizedUrl = normalizeRequestUrl(url)
  return normalizedUrl.startsWith('/v1/public/')
}

const refreshAccessToken = async () => {
  const refreshToken = localStorage.getItem('travi_refresh_token');

  if (!refreshToken) {
    throw new Error('Missing refresh token');
  }

  if (!refreshPromise) {
    refreshPromise = axios
      .post<FlexibleAuthResponse>(`${API_BASE_URL}/v1/auth/refresh`, { refreshToken })
      .then((response) => {
        saveAuthResponse(response.data);
        window.dispatchEvent(new Event('auth:refresh'));
        return response.data;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }

  return refreshPromise;
};

api.interceptors.request.use((config) => {
  const token = tokenStorage.getAccessToken();

  if (token && !isPublicAuthRequest(config.url)) {
    config.headers.Authorization = `${tokenStorage.getTokenType()} ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as RetriableRequestConfig | undefined;
    const status = error.response?.status;
    const isAuthRequest = isPublicAuthRequest(originalRequest?.url);

    if (status !== 401 || !originalRequest || originalRequest._retry || isAuthRequest) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      const auth = await refreshAccessToken();
      const newAccessToken = getAccessTokenFromAuthResponse(auth);

      if (!newAccessToken) {
        tokenStorage.clearTokens();
        window.dispatchEvent(new Event('auth:logout'));
        return Promise.reject(new Error('Refresh thành công nhưng backend không trả access token mới.'));
      }

      originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

      return api(originalRequest);
    } catch (refreshError) {
      tokenStorage.clearTokens();
      localStorage.removeItem('travi_access_token');
      localStorage.removeItem('travi_refresh_token');
      localStorage.removeItem('travi_token_type');
      window.dispatchEvent(new Event('auth:logout'));
      return Promise.reject(refreshError);
    }
  }
);
