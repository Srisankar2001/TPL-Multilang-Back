package com.example.multi_lang.Service;

import com.example.multi_lang.DTO.CreateProductRequest;
import com.example.multi_lang.DTO.ProductDTO;
import com.example.multi_lang.DTO.ResponseDTO;
import com.example.multi_lang.Entity.Language;
import com.example.multi_lang.Entity.Product;
import com.example.multi_lang.Entity.Translation;
import com.example.multi_lang.Repository.LanguageRepository;
import com.example.multi_lang.Repository.ProductRepository;
import com.example.multi_lang.Repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    LanguageRepository languageRepository;
    @Autowired
    TranslationRepository translationRepository;
    public ResponseDTO<?> getAllProducts(String code) {
        Optional<Language> language = languageRepository.findByCode(code);
        if(language.isEmpty()){
            return ResponseDTO.builder()
                    .status(false)
                    .message("Language Not Found")
                    .build();
        }
        Set<ProductDTO> productDTOSet = new HashSet<>();
        List<Product> products = productRepository.findAll();
        for(Product product : products){
            Optional<Translation> existingNameTranslation = translationRepository.findByTableNameAndColumnNameAndRowIndexAndLanguage("product","name",product.getId(),language.get());
            Optional<Translation> existingDescriptionTranslation = translationRepository.findByTableNameAndColumnNameAndRowIndexAndLanguage("product","description",product.getId(),language.get());
            if(existingNameTranslation.isPresent() && existingDescriptionTranslation.isPresent()){
                Translation nameTranslation = existingNameTranslation.get();
                Translation descriptionTranslation = existingDescriptionTranslation.get();
                productDTOSet.add(
                        ProductDTO.builder()
                                .id(product.getId())
                                .price(product.getPrice())
                                .stock(product.getStock())
                                .name(nameTranslation.getValue())
                                .description(descriptionTranslation.getValue())
                                .build()
                );
            }
        }
        return ResponseDTO.builder()
                .status(true)
                .data(productDTOSet)
                .build();
    }

    public ResponseDTO<?> getProduct(String code, int productId) {
        Optional<Language> language = languageRepository.findByCode(code);
        if(language.isEmpty()){
            return ResponseDTO.builder()
                    .status(false)
                    .message("Language Not Found")
                    .build();
        }
        Optional<Product> existingProduct = productRepository.findById(productId);
        if(existingProduct.isEmpty()){
            return ResponseDTO.builder()
                    .status(false)
                    .message("Product Not Found")
                    .build();
        }
        Product product = existingProduct.get();
        Optional<Translation> existingNameTranslation = translationRepository.findByTableNameAndColumnNameAndRowIndexAndLanguage("product","name",product.getId(),language.get());
        Optional<Translation> existingDescriptionTranslation = translationRepository.findByTableNameAndColumnNameAndRowIndexAndLanguage("product","description",product.getId(),language.get());
        if(existingNameTranslation.isPresent() && existingDescriptionTranslation.isPresent()){
            Translation nameTranslation = existingNameTranslation.get();
            Translation descriptionTranslation = existingDescriptionTranslation.get();
            return ResponseDTO.builder()
                    .status(true)
                    .data(ProductDTO.builder()
                            .id(product.getId())
                            .stock(product.getStock())
                            .price(product.getPrice())
                            .name(nameTranslation.getValue())
                            .description(descriptionTranslation.getValue())
                            .build())
                    .build();
        }else{
            return ResponseDTO.builder()
                    .status(false)
                    .message("Data Not Found")
                    .build();
        }
    }

    public ResponseDTO<?> createProduct(CreateProductRequest createProductRequest) {
        //Validation
        String name = createProductRequest.getName().get("en");
        String description = createProductRequest.getDescription().get("en");
        if(name == null || description == null){
            return ResponseDTO.builder()
                    .status(false)
                    .message("English Input is Missing")
                    .build();
        }
        Optional<Product> existingProduct = productRepository.findByName(name);
        if(existingProduct.isPresent()){
            return ResponseDTO.builder()
                    .status(false)
                    .message("Product already exist")
                    .build();
        }
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(createProductRequest.getPrice())
                .stock(createProductRequest.getStock())
                .build();
        Product savedProduct;
        try{
            savedProduct = productRepository.saveAndFlush(product);
        }catch (Exception e){
            return ResponseDTO.builder()
                    .status(false)
                    .message("Internal Server Error")
                    .build();
        }
        for(Map.Entry<String,String> entry : createProductRequest.getName().entrySet()){
            String code = entry.getKey();
            String nameValue = entry.getValue();
            String descriptionValue = createProductRequest.getDescription().get(code);
            Optional<Language> language = languageRepository.findByCode(code);
            if(language.isEmpty()){
                return ResponseDTO.builder()
                        .status(false)
                        .message("Language Not Found")
                        .build();
            }
            if(descriptionValue == null){
                return ResponseDTO.builder()
                        .status(false)
                        .message("Data Not Found")
                        .build();
            }
            Translation nameTranslation = Translation.builder()
                    .tableName("product")
                    .columnName("name")
                    .rowIndex(savedProduct.getId())
                    .language(language.get())
                    .value(nameValue)
                    .build();

            Translation descriptionTranslation = Translation.builder()
                    .tableName("product")
                    .columnName("description")
                    .rowIndex(savedProduct.getId())
                    .language(language.get())
                    .value(descriptionValue)
                    .build();

            try{
                translationRepository.save(nameTranslation);
                translationRepository.save(descriptionTranslation);
            }catch (Exception e){
                return ResponseDTO.builder()
                        .status(false)
                        .message("Internal Server Error")
                        .build();
            }
        }
        return ResponseDTO.builder()
                .status(true)
                .message("Product Created Successfully")
                .build();
    }
}
