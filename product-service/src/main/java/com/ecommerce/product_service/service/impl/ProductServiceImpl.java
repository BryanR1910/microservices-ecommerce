package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.dto.ProductRequestDTO;
import com.ecommerce.product_service.dto.ProductResponseDTO;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.mapper.ProductMapper;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        Product product = productMapper.toProduct(requestDTO);
        Product savedProduct = productRepository.save(product);

        log.info("Product {} guardado", savedProduct.getName());
        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        List<ProductResponseDTO> responseDto = productRepository.findAll().stream()
                .map(productMapper::toResponseDTO)
                .toList();
        return responseDto;
    }

    @Override
    public void deleteProduct(String id) {

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
        log.info("Product con el id:{} fue eliminado", id);
    }

    @Override
    public ProductResponseDTO getProductById(String id) {
        Product product =
                productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO updateProduct(String id, ProductRequestDTO productRequest) {
        Product product =
                productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productMapper.updateProductFromRequest(productRequest, product);

        Product updatedProduct = productRepository.save(product);
        log.info("Product {} actualizado", updatedProduct.getName());
        return productMapper.toResponseDTO(updatedProduct);
    }
}
