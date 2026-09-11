package com.ota.travi.Security;

import com.ota.travi.Entity.User;
import com.ota.travi.Entity.VaiTro;
import com.ota.travi.Enum.TrangThaiUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }


    public String getName() {
        return user.getUsername();
    }

    public String getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public VaiTro getRoleValue() {
        return user.getVaiTro();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ví dụ: 1 = ROLE_ADMIN, 2 = ROLE_USER
        String roleName = switch (user.getVaiTro().getTen()) {
            case "ADMIN" -> "ROLE_ADMIN";
            case "USER" -> "ROLE_USER";
            default -> "ROLE_GUEST";
        };
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return user.getMatKhau();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Bạn có thể dùng status để kiểm tra
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Bạn có thể dùng activatedStatus để kiểm tra
    }


}
