package com.ota.travi.Controller;

import com.ota.travi.Constant.ApiEndpoints;
import com.ota.travi.Dto.reponse.LoginResponse;
import com.ota.travi.Dto.reponse.ResponseObject;
import com.ota.travi.Dto.request.LoginRequest;
import com.ota.travi.Dto.request.RegisterRequest;
import com.ota.travi.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userAuthController")
@RequestMapping(ApiEndpoints.USER_AUTH)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest)
    {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loginResponse);
    }

    @PostMapping("/register")
    ResponseEntity<ResponseObject> registerUser(@Valid @RequestBody RegisterRequest registerUser){
        ResponseObject responseObject =  authService.registerUser(registerUser);
        System.out.println(registerUser);
        return ResponseEntity.status(responseObject.status())
                .body(responseObject);
    }
}
