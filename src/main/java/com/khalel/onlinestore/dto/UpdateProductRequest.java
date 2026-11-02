package com.khalel.onlinestore.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must contain 2 to 100 characters") String name,
        @Size(max = 1000, message = "Description must not exceed 1000 characters") String description,
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        @Digits(integer = 15, fraction = 4, message = "Price allows 15 integer and 4 decimal digits") BigDecimal price,
        @NotBlank(message = "Category is required") String category,
        Boolean inStock) {}
