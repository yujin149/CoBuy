package com.cobuy.service;

import com.cobuy.repository.AdminRepository;
import com.cobuy.repository.SellerRepository;
import com.cobuy.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.cobuy.service.CustomUserDetails;

import java.util.Collections;

//통합 인증 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 현재 요청에서 role 파라미터 가져오기
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestedRole = request.getParameter("role");

        log.info("Attempting login with username: {} and role: {}", username, requestedRole);

        if ("ADMIN".equals(requestedRole)) {
            return adminRepository.findByAdminId(username)
                .map(admin -> {
                    if (!"ADMIN".equals(admin.getRole().name())) {
                        throw new BadCredentialsException("Invalid role for this account");
                    }
                    return new CustomUserDetails(
                        admin.getAdminId(),
                        admin.getAdminPW(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")),
                        admin.getAdminName(),
                        admin.getAdminEmail(),
                        admin.getAdminShopName()
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
        }
        else if ("SELLER".equals(requestedRole)) {
            return sellerRepository.findBySellerId(username)
                .map(seller -> {
                    if (!"SELLER".equals(seller.getRole().name())) {
                        throw new BadCredentialsException("Invalid role for this account");
                    }
                    return new CustomUserDetails(
                        seller.getSellerId(),
                        seller.getSellerPW(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_SELLER")),
                        seller.getSellerName(),
                        seller.getSellerEmail(),
                        null
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));
        }
        else if ("USER".equals(requestedRole)) {
            return userRepository.findByUserId(username)
                .map(user -> {
                    if (!"USER".equals(user.getRole().name())) {
                        throw new BadCredentialsException("Invalid role for this account");
                    }
                    return new CustomUserDetails(
                        user.getUserId(),
                        user.getUserPW(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                        user.getUserName(),
                        user.getUserEmail(),
                        null
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

        throw new BadCredentialsException("Invalid role specified");
    }
} 