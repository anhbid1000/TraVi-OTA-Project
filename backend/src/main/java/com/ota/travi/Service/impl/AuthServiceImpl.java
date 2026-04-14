package com.ota.travi.Service.impl;

import com.ota.travi.Dto.reponse.LoginResponse;
import com.ota.travi.Dto.reponse.ResponseObject;
import com.ota.travi.Dto.request.LoginRequest;
import com.ota.travi.Dto.request.RegisterRequest;
import com.ota.travi.Entity.User;
import com.ota.travi.Entity.VaiTro;
import com.ota.travi.Exception.DuplicateFieldException;
import com.ota.travi.Repository.UserRepository;
import com.ota.travi.Repository.VaitroRepository;
import com.ota.travi.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final VaitroRepository vaitroRepository;
    private final ModelMapper modelMapper;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, VaitroRepository vaitroRepository, ModelMapper modelMappper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.vaitroRepository = vaitroRepository;
        this.modelMapper = modelMappper;
    }

    @Autowired
    @Lazy
    public PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.email());

        if (user == null) {
            return new LoginResponse(false, "Tài khoản không tồn tại", null, null);

        }
        if (!passwordEncoder.matches(loginRequest.matKhau(), user.getMatKhau())) {
            return new LoginResponse(false, "Sai mật khẩu", null, null);
        }
        String token = "haha";
        return new LoginResponse(true, "Đăng nhập thành công", token, user.getUsername());
    }

    public Map<String, String> checkDuplicated(RegisterRequest registerUser) {
        Map<String, String> errors = new HashMap<>();

        Map<String,Boolean> checks = Map.of(
                "username" , userRepository.existsUserByUsername(registerUser.username()),
                "email" , userRepository.existsUserByEmail(registerUser.email()),
                "phone" , userRepository.existsUserBySoDienThoai(registerUser.soDienThoai())
        );

        Map<String,String> messages = Map.of(
                "username" , "Username is already taken!",
                "email" , "Email already exists",
                "phone" , "Phone already exists"
        );

        checks.forEach((error,exists) -> {
            if(exists)
            {
                errors.put(error,messages.get(error));
            }
        });
        return errors;
    }

    public ResponseObject registerUser(RegisterRequest registerUser) {
        Map<String, String> errors = checkDuplicated(registerUser);
        System.out.println("Truoc khi model");
        System.out.println(registerUser);
        if (!errors.isEmpty()) {
            throw new DuplicateFieldException(errors);
        }
        User user = new User();
        user.setEmail(registerUser.email());
        user.setUsername(registerUser.username());
        user.setHoTen(registerUser.hoTen());
        user.setNgaySinh(registerUser.ngaySinh());
        user.setGioiTinh(registerUser.gioiTinh());
        user.setSoDienThoai(registerUser.soDienThoai());
        user.setMatKhau(passwordEncoder.encode(registerUser.matKhau()));
        VaiTro vaiTro = vaitroRepository.findByTen("USER");
        if (vaiTro == null) {
            vaiTro = new VaiTro();
            vaiTro.setTen("USER");
            vaiTro.setMoTa("Vai trò mặc định cho người dùng");
            vaitroRepository.save(vaiTro);
        }
        user.setVaiTro(vaiTro);
        System.out.println(user);
        userRepository.save(user);
        return new ResponseObject("Dang ky thanh cong", HttpStatus.CREATED.value(), registerUser);
    }
}
