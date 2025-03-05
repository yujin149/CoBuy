package com.cobuy.service;

import com.cobuy.dto.CartItemDto;
import com.cobuy.dto.ProductImageDto;
import com.cobuy.entity.*;
import com.cobuy.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final SellerRepository sellerRepository;

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return authentication.getName();
    }

    private String getCurrentMemberType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        // ROLE_ 접두사 제거
        return role.replace("ROLE_", "");
    }

    @Override
    public List<CartItemDto> getCartItems() {
        String userId = getCurrentUserId();
        String memberType = getCurrentMemberType();
        String adminId = null;
        String sellerId = null;

        List<Cart> cartItems;
        switch (memberType) {
            case "USER":
                cartItems = cartRepository.findByUser_UserIdOrderByRegTimeDesc(userId);
                break;
            case "ADMIN":
                cartItems = cartRepository.findByAdmin_AdminIdOrderByRegTimeDesc(adminId);
                break;
            case "SELLER":
                cartItems = cartRepository.findBySeller_SellerIdOrderByRegTimeDesc(sellerId);
                break;
            default:
                throw new IllegalArgumentException("Invalid member type: " + memberType);
        }

        return cartItems.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addToCart(CartItemDto cartItemDto) {
        System.out.println("\n=== CartServiceImpl.addToCart Start ===");
        System.out.println("CartItemDto: " + cartItemDto);

        // 현재 로그인한 사용자 정보 가져오기
        String userId = getCurrentUserId();
        String memberType = getCurrentMemberType();
        System.out.println("Current User ID: " + userId);
        System.out.println("Current Member Type: " + memberType);

        // 상품 정보 조회
        Product product = productRepository.findById(cartItemDto.getId())
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        System.out.println("Found Product: " + product);

        // 상품의 adminId 가져오기
        String adminId = product.getAdmin().getAdminId();
        System.out.println("Product Admin ID: " + adminId);

        // 기존 장바구니 항목 확인
        Cart existingCart = cartRepository.findByMemberTypeIdAndProductId(userId, adminId, null, product.getId());
        System.out.println("Existing Cart Item: " + existingCart);

        if (existingCart != null) {
            // 기존 항목이 있으면 수량 업데이트
            existingCart.updateQuantity(existingCart.getQuantity() + cartItemDto.getQuantity());
            cartRepository.save(existingCart);
            System.out.println("Updated existing cart item quantity");
        } else {
            // 새로운 장바구니 항목 생성
            Cart cart;
            switch (memberType) {
                case "USER":
                    User user = userRepository.findByUserId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
                    cart = Cart.createCart(user, product, cartItemDto.getQuantity(),
                        cartItemDto.getSelectedOptions(), cartItemDto.getProductSalePrice(),
                        cartItemDto.getProductFee(), cartItemDto.getImageUrl());
                    break;
                case "ADMIN":
                    Admin admin = adminRepository.findByAdminId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));
                    cart = Cart.createCart(admin, product, cartItemDto.getQuantity(),
                        cartItemDto.getSelectedOptions(), cartItemDto.getProductSalePrice(),
                        cartItemDto.getProductFee(), cartItemDto.getImageUrl());
                    break;
                case "SELLER":
                    Seller seller = sellerRepository.findBySellerId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("셀러를 찾을 수 없습니다."));
                    cart = Cart.createCart(seller, product, cartItemDto.getQuantity(),
                        cartItemDto.getSelectedOptions(), cartItemDto.getProductSalePrice(),
                        cartItemDto.getProductFee(), cartItemDto.getImageUrl());
                    break;
                default:
                    throw new IllegalArgumentException("Invalid member type: " + memberType);
            }
            cartRepository.save(cart);
            System.out.println("Created new cart item");
        }

        System.out.println("=== CartServiceImpl.addToCart End ===\n");
    }

    @Override
    public void updateCartItem(CartItemDto cartItemDto) {
        Cart cart = cartRepository.findById(cartItemDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));

        cart.updateQuantity(cartItemDto.getQuantity());
        cart.updateSelectedOptions(cartItemDto.getSelectedOptions());
    }

    @Override
    public void deleteCartItem(Long id) {
        Cart cart = cartRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));
        cartRepository.delete(cart);
    }

    private CartItemDto convertToDto(Cart cart) {
        CartItemDto dto = new CartItemDto();
        dto.setId(cart.getId());
        // adminId를 String으로 설정
        if (cart.getProduct().getAdmin() != null && cart.getProduct().getAdmin().getAdminId() != null) {
            dto.setAdminId(cart.getProduct().getAdmin().getAdminId());
        }
        dto.setProductCode(cart.getProduct().getProductCode());
        dto.setProductName(cart.getProduct().getProductName());
        dto.setSelectedOptions(cart.getSelectedOptions());
        dto.setQuantity(cart.getQuantity());
        dto.setProductOriPrice(cart.getProduct().getProductOriPrice());
        dto.setProductSalePrice(cart.getProductSalePrice());
        dto.setProductStock(cart.getProduct().getProductStock());
        dto.setProductFee(cart.getProductFee());
        dto.setImageUrl(cart.getImageUrl());
        // productImages 설정
        dto.setProductImages(cart.getProduct().getProductImages().stream()
            .map(image -> {
                ProductImageDto imageDto = new ProductImageDto();
                imageDto.setId(image.getId());
                imageDto.setImageName(image.getImageName());
                imageDto.setImageUrl(image.getImageUrl());
                imageDto.setImageType(image.getImageType());
                imageDto.setRepImageYn(image.isRepImageYn());
                return imageDto;
            })
            .collect(Collectors.toList()));
        return dto;
    }
}