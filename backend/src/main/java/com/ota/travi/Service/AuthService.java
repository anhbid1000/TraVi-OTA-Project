package com.ota.travi.Service;

import com.ota.travi.Dto.reponse.LoginResponse;
import com.ota.travi.Dto.reponse.ResponseObject;
import com.ota.travi.Dto.request.LoginRequest;
import com.ota.travi.Dto.request.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    ResponseObject registerUser(RegisterRequest registerUser);
}
