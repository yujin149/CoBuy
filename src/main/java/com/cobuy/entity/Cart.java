package com.cobuy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart")
@Getter
@Setter
public class Cart extends BaseEntity {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 일반 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin; // 관리자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller; // 셀러

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // 장바구니에 담은 상품

    @Column(nullable = false)
    private int quantity; // 수량

    @Column(name = "selected_options")
    private String selectedOptions; // 선택된 옵션 정보 (JSON 형식으로 저장)

    @Column(name = "product_sale_price", nullable = false)
    private int productSalePrice; // 상품 판매가 (장바구니 담을 때의 가격)

    @Column(name = "product_fee", nullable = false)
    private int productFee; // 배송비

    @Column(name = "image_url")
    private String imageUrl; // 상품 이미지 URL

    // 장바구니 아이템 생성 메서드 (일반 회원용)
    public static Cart createCart(User user, Product product, int quantity,
                                  String selectedOptions, int productSalePrice,
                                  int productFee, String imageUrl) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        cart.setSelectedOptions(selectedOptions);
        cart.setProductSalePrice(productSalePrice);
        cart.setProductFee(productFee);
        cart.setImageUrl(imageUrl);
        return cart;
    }

    // 장바구니 아이템 생성 메서드 (관리자용)
    public static Cart createCart(Admin admin, Product product, int quantity,
                                  String selectedOptions, int productSalePrice,
                                  int productFee, String imageUrl) {
        Cart cart = new Cart();
        cart.setAdmin(admin);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        cart.setSelectedOptions(selectedOptions);
        cart.setProductSalePrice(productSalePrice);
        cart.setProductFee(productFee);
        cart.setImageUrl(imageUrl);
        return cart;
    }

    // 장바구니 아이템 생성 메서드 (셀러용)
    public static Cart createCart(Seller seller, Product product, int quantity,
                                  String selectedOptions, int productSalePrice,
                                  int productFee, String imageUrl) {
        Cart cart = new Cart();
        cart.setSeller(seller);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        cart.setSelectedOptions(selectedOptions);
        cart.setProductSalePrice(productSalePrice);
        cart.setProductFee(productFee);
        cart.setImageUrl(imageUrl);
        return cart;
    }

    // 장바구니 수량 변경 메서드
    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    // 장바구니 옵션 변경 메서드
    public void updateSelectedOptions(String selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    // 회원 ID 조회 메서드
    public String getMemberId() {
        if (user != null) {
            return user.getUserId();
        } else if (admin != null) {
            return admin.getAdminId();
        } else if (seller != null) {
            return seller.getSellerId();
        }
        return null;
    }

    // 회원 타입 조회 메서드
    public String getMemberType() {
        if (user != null) {
            return "USER";
        } else if (admin != null) {
            return "ADMIN";
        } else if (seller != null) {
            return "SELLER";
        }
        return null;
    }
}