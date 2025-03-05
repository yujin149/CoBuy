package com.cobuy.service;

import com.cobuy.dto.CartItemDto;

import java.util.List;

public interface CartService {
    List<CartItemDto> getCartItems();
    void addToCart(CartItemDto cartItemDto);
    void updateCartItem(CartItemDto cartItemDto);
    void deleteCartItem(Long id);
}