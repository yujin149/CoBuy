package com.cobuy.service;

import com.cobuy.dto.SellerDto;
import com.cobuy.entity.Seller;
import com.cobuy.repository.SellerRepository;
import com.cobuy.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
@RequiredArgsConstructor
public class SellerService {
    
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(SellerService.class);

    private String combinePhoneNumber(SellerDto sellerDto) {
        return sellerDto.getSellerPhone01() + "-" + 
               sellerDto.getSellerPhone2() + "-" + 
               sellerDto.getSellerPhone3();
    }

    public void join(SellerDto sellerDto) {
        Seller seller = new Seller();
        seller.setSellerId(sellerDto.getSellerId());
        seller.setSellerPW(passwordEncoder.encode(sellerDto.getSellerPW()));
        seller.setSellerNickName(sellerDto.getSellerNickName());
        seller.setSellerName(sellerDto.getSellerName());
        seller.setSellerPhone(combinePhoneNumber(sellerDto));
        seller.setSellerEmail(sellerDto.getSellerEmail());
        seller.setSellerAddress(sellerDto.getSellerAddress());
        seller.setSellerUrl(sellerDto.getSellerUrl());
        seller.setSellerContents(sellerDto.getSellerContents());
        seller.setRole(Role.SELLER);
        seller.setProductCategories(sellerDto.getProductCategories());

        sellerRepository.save(seller);
    }

    @Transactional(readOnly = true)
    public boolean checkDuplicateId(String sellerId) {
        boolean exists = sellerRepository.existsBySellerId(sellerId);
        log.info("Checking duplicate ID: {} - Exists: {}", sellerId, exists);
        return exists;
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return sellerRepository.existsBySellerEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return sellerRepository.existsBySellerPhone(phone);
    }
} 