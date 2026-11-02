package com.khalel.onlinestore.service;

import com.khalel.onlinestore.dto.*;
import com.khalel.onlinestore.entity.Product;
import com.khalel.onlinestore.exception.ProductNotFoundException;
import com.khalel.onlinestore.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductResponse> findAll() {
        return repository.findAllByOrderByIdAsc().stream().map(this::toResponse).toList();
    }

    public ProductResponse findById(Long id) {
        return toResponse(getProduct(id));
    }

    public List<ProductResponse> findByCategory(String category) {
        return repository.findByCategoryIgnoreCaseOrderByIdAsc(category)
                .stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> search(String name) {
        return repository.findByNameContainingIgnoreCaseOrderByIdAsc(name)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        return toResponse(repository.saveAndFlush(product));
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = getProduct(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setInStock(request.inStock() == null ? true : request.inStock());
        return toResponse(repository.saveAndFlush(product));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getProduct(id));
    }

    private Product getProduct(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getCategory(), product.getInStock(),
                product.getCreatedAt(), product.getUpdatedAt());
    }
}
