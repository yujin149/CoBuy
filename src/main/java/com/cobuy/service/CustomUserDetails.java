package com.cobuy.service;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
    private final String name;
    private final String email;
    private final String role;
    private final String adminShopName;

    public CustomUserDetails(String username, String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String name, String email, String adminShopName) {
        super(username, password, authorities);
        this.name = name;
        this.email = email;
        this.role = authorities.stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .map(auth -> auth.replace("ROLE_", ""))
            .orElse(null);
        this.adminShopName = adminShopName;
    }
} 