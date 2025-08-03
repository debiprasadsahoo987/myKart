package com.mykart.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotBlank
    @Size(min = 3, message = "Product name should be at least 3 characters.")
    private String productName;

    @NotBlank
    @Size(min = 6, message = "Product description should be at least 6 characters.")
    private String productDescription;
    private String productImage;
    private Integer productQuantity;
    private double productPrice;
    private double productDiscount;
    private double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
