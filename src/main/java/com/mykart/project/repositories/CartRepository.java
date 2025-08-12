package com.mykart.project.repositories;

import com.mykart.project.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.user.email =  ?1")
    Cart findKartByEmail(String email);

    @Query("select c from Cart c where c.user.email = ?1 and c.cartId = ?2")
    Cart findKartByEmailAndCartId(String emailId, Long cartId);


}
