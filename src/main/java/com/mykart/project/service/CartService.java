package com.mykart.project.service;

import com.mykart.project.payload.CartDTO;

public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);
}
