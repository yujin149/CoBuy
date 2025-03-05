package com.cobuy.controller;

import com.cobuy.dto.CartItemDto;
import com.cobuy.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String cartList(Model model) {
        model.addAttribute("pageTitle", "장바구니");
        List<CartItemDto> cartItems = cartService.getCartItems();

        // 총 상품 금액 계산
        int totalProductPrice = cartItems.stream()
            .mapToInt(item -> item.getProductSalePrice() * item.getQuantity())
            .sum();

        // 총 배송비 계산 (상품당 한 번만)
        int totalDeliveryFee = cartItems.stream()
            .mapToInt(CartItemDto::getProductFee)
            .distinct()
            .sum();

        // 총 결제 예상 금액
        int totalPrice = totalProductPrice + totalDeliveryFee;

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalProductPrice", totalProductPrice);
        model.addAttribute("totalDeliveryFee", totalDeliveryFee);
        model.addAttribute("totalPrice", totalPrice);

        return "mypage/cart";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestBody CartItemDto cartItemDto) {
        try {
            cartService.addToCart(cartItemDto);
            return ResponseEntity.ok().body("장바구니에 상품이 추가되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateCartItem(@RequestBody CartItemDto cartItemDto) {
        try {
            System.out.println("=== CartController.updateCartItem Start ===");
            System.out.println("CartItemDto: " + cartItemDto);
            System.out.println("Selected Options: " + cartItemDto.getSelectedOptions());

            cartService.updateCartItem(cartItemDto);

            System.out.println("=== CartController.updateCartItem End ===");
            return ResponseEntity.ok().body("장바구니가 수정되었습니다.");
        } catch (Exception e) {
            System.err.println("Error updating cart item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCartItem(@PathVariable Long id) {
        try {
            cartService.deleteCartItem(id);
            return ResponseEntity.ok().body("장바구니에서 상품이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}