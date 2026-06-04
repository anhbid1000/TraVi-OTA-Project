import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { AuthProvider } from './contexts/AuthContext.tsx'
import { GoogleOAuthProviderWrapper } from './contexts/GoogleOAuthProviderWrapper.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <GoogleOAuthProviderWrapper>
      <AuthProvider>
        <App />
      </AuthProvider>
    </GoogleOAuthProviderWrapper>
  </StrictMode>,
)
