package com.cobuy.controller;

import com.cobuy.dto.CartItemDto;
import com.cobuy.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart")
    public String cartList(Model model) {
        model.addAttribute("pageTitle", "장바구니");
        // TODO: 실제 로그인한 사용자의 장바구니 목록을 가져오도록 수정
        List<CartItemDto> cartItems = cartService.getCartItems();

        // 총 상품 금액 계산
        int totalProductPrice = cartItems.stream()
            .mapToInt(item -> item.getProductSalePrice() * item.getQuantity())
            .sum();

        // 총 배송비 계산 (각 상품의 배송비 합계)
        int totalDeliveryFee = cartItems.stream()
            .mapToInt(CartItemDto::getProductFee)
            .sum();

        // 총 결제 예상 금액
        int totalPrice = totalProductPrice + totalDeliveryFee;

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalProductPrice", totalProductPrice);
        model.addAttribute("totalDeliveryFee", totalDeliveryFee);
        model.addAttribute("totalPrice", totalPrice);

        return "mypage/cart";
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public String addToCart(@RequestBody CartItemDto cartItemDto) {
        try {
            cartService.addToCart(cartItemDto);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/cart/update")
    @ResponseBody
    public String updateCartItem(@RequestBody CartItemDto cartItemDto) {
        try {
            cartService.updateCartItem(cartItemDto);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/cart/delete/{id}")
    @ResponseBody
    public String deleteCartItem(@PathVariable Long id) {
        try {
            cartService.deleteCartItem(id);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
}