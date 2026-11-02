package com.khalel.onlinestore.repository;

import com.khalel.onlinestore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByIdAsc();
    List<Product> findByCategoryIgnoreCaseOrderByIdAsc(String category);
    List<Product> findByNameContainingIgnoreCaseOrderByIdAsc(String name);
}
