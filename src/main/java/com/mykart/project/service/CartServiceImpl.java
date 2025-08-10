package com.mykart.project.service;

import com.mykart.project.exceptions.APIException;
import com.mykart.project.exceptions.ResourceNotFoundException;
import com.mykart.project.model.Cart;
import com.mykart.project.model.CartItem;
import com.mykart.project.model.Product;
import com.mykart.project.payload.CartDTO;
import com.mykart.project.payload.ProductDTO;
import com.mykart.project.repositories.CartItemRepository;
import com.mykart.project.repositories.CartRepository;
import com.mykart.project.repositories.ProductRepository;
import com.mykart.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtil authUtil;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        //1. Find an existing cart or create a new one
        Cart cart = createCart();
        //2. Retrieve product details
        Product product = productRepository.findByProductId(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        //3. Perform validations
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),
                productId
        );

        if (cartItem != null) {
            throw new APIException(("Product " + product.getProductName() + " has already been added to the cart."));
        }

        if (product.getProductQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available in your cart.");
        }

        if (product.getProductQuantity() < quantity) {
            throw new APIException("Please make an order of the " + product.getProductName() + " less than or equal to the quantity of " + product.getProductQuantity() + ".");
        }
        //4. Create cart item

        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getProductDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        //5. Save cart item

        cartItemRepository.save(newCartItem);

        //Reduce stock if item is added to cart
        product.setProductQuantity(product.getProductQuantity());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setProductQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productDTOStream.toList());
        //6. Return updated cart
        return cartDTO;
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findKartByEmail((authUtil.loggedInEmail()));
        if (userCart != null) {
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }
}
