package com.cobuy.service;

import com.cobuy.dto.UserDto;
import com.cobuy.entity.User;
import com.cobuy.repository.UserRepository;
import com.cobuy.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private String combinePhoneNumber(UserDto userDto) {
        return userDto.getUserPhone01() + "-" +
            userDto.getUserPhone2() + "-" +
            userDto.getUserPhone3();
    }

    public void join(UserDto userDto) {
        User user = new User();
        user.setUserId(userDto.getUserId());
        user.setUserPW(passwordEncoder.encode(userDto.getUserPW()));
        user.setUserName(userDto.getUserName());
        user.setUserPhone(combinePhoneNumber(userDto));
        user.setUserEmail(userDto.getUserEmail());
        user.setUserAddress(userDto.getUserAddress());
        user.setUserBirth(userDto.getUserBirth());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean checkDuplicateId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByUserEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return userRepository.existsByUserPhone(phone);
    }
} 