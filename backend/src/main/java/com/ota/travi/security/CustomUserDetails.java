package com.ota.travi.security;

import com.ota.travi.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    // Cấp quyền (Role) cho User. Spring Security quy chuẩn Role nên bắt đầu bằng "ROLE_"
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getVaiTro() == null || user.getVaiTro().getTen() == null) {
            return Collections.emptyList();
        }
        String tenVaiTro = user.getVaiTro().getTen();
        String role = tenVaiTro.startsWith("ROLE_") ? tenVaiTro : "ROLE_" + tenVaiTro;
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getMatKhau();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // Các cấu hình khóa tài khoản (Tạm thời để true là luôn mở)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getTrangThai().name().equals("HOAT_DONG");
    }
}
