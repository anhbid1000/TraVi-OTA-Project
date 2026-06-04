package com.ota.travi.security;

import com.ota.travi.entity.User;
import com.ota.travi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) {
        return loadUserByEmail(email);
    }

    public CustomUserDetails loadUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Lỗi: Không tìm thấy tài khoản với email: " + email));

        return new CustomUserDetails(user);
    }

    public CustomUserDetails loadUserByUsernameValue(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Lỗi: Không tìm thấy tài khoản với username: " + username));


        return new CustomUserDetails(user);
    }
}
