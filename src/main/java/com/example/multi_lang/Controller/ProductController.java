package com.example.multi_lang.Controller;

import com.example.multi_lang.DTO.CreateProductRequest;
import com.example.multi_lang.DTO.ResponseDTO;
import com.example.multi_lang.DTO.UpdateProductRequest;
import com.example.multi_lang.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/{code}")
    public ResponseDTO<?> getAllProducts(@PathVariable String code){
        return productService.getAllProducts(code);
    }

    @GetMapping("/{code}/{productId}")
    public ResponseDTO<?> getProduct(@PathVariable String code, @PathVariable int productId){
        return productService.getProduct(code,productId);
    }

    @PostMapping("")
    public ResponseDTO<?> createProduct(@RequestBody CreateProductRequest createProductRequest){
        return productService.createProduct(createProductRequest);
    }

}
