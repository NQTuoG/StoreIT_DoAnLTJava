package com.example.StoreIT.entity;


import com.example.StoreIT.validator.nnotation.ValidCategoryId;
import com.example.StoreIT.validator.nnotation.ValidUserId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotEmpty(message = "Title must not be empty")
    @Size(max = 250, min = 1, message = "Title must be less than 250 characters")
    private String name;

    @Column(name = "title")
    @NotEmpty(message = "Title must not be empty")
    @Size(max = 250, min = 1, message = "Title must be less than 250 characters")
    private String title;

    @Column(name = "supplier")
    private String supplier;

    @Column(name = "image")
    private String image;

    @Column(name = "quantity")
    @NotNull(message = "Price is required")
    private String quantity;

    @Column(name = "price")
    @NotNull(message = "Price is required")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @ValidCategoryId
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ValidUserId
    private User user;
}
