package com.cobuy.service;

import com.cobuy.constant.ProductCategory;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    //셀러 검색 메서드 추가
    @Transactional(readOnly = true)
    public List<Map<String, Object>> searchSellers(String searchType, String sellerId, String sellerNickName) {
        List<Seller> sellers;

        if ("전체".equals(searchType)) {
            String searchTerm = "";
            if (sellerId != null && !sellerId.isEmpty()) {
                searchTerm = sellerId;
            } else if (sellerNickName != null && !sellerNickName.isEmpty()) {
                searchTerm = sellerNickName;
            }

            if (!searchTerm.isEmpty()) {
                sellers = sellerRepository.findBySellerIdContainingOrSellerNickNameContaining(searchTerm, searchTerm);
            } else {
                sellers = new ArrayList<>();
            }
        } else if ("아이디".equals(searchType)) {
            sellers = sellerRepository.findBySellerIdContaining(sellerId);
        } else if ("인플루언서명".equals(searchType)) {
            sellers = sellerRepository.findBySellerNickNameContaining(sellerNickName);
        } else {
            sellers = new ArrayList<>();
        }

        return sellers.stream()
            .map(seller -> {
                Map<String, Object> result = new HashMap<>();
                result.put("sellerId", seller.getSellerId());
                result.put("sellerNickName", seller.getSellerNickName());
                result.put("sellerUrl", seller.getSellerUrl());
                result.put("productCategories", seller.getProductCategories().stream()
                    .map(ProductCategory::getDisplayName)  // enum의 displayName으로 변환
                    .collect(Collectors.toList()));
                return result;
            })
            .collect(Collectors.toList());
    }


} 