package com.mykart.project.service;

import com.mykart.project.exceptions.APIException;
import com.mykart.project.exceptions.ResourceNotFoundException;
import com.mykart.project.model.Category;
import com.mykart.project.model.Product;
import com.mykart.project.payload.ProductDTO;
import com.mykart.project.payload.ProductResponse;
import com.mykart.project.repositories.CategoryRepository;
import com.mykart.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isProductNotPresent = true;

        List<Product> products = category.getProducts();
        for(Product product:products){
            if (product.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }

        if(isProductNotPresent){
            Product product = modelMapper.map(productDTO, Product.class);
            product.setProductImage("default.png");
            product.setCategory(category);
            double specialPrice =  product.getProductPrice() - ((product.getProductDiscount() * 0.01) * product.getProductPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        } else {
            throw new APIException("Product already exists");
        }

    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        if(products.isEmpty()){
            throw new APIException("No products found");
        }

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        List<Product> products = productRepository.findByCategoryOrderByProductPriceAsc(category);

        if(products.isEmpty()){
            throw new APIException("No products found for the category: " + category.getCategoryName());
        }

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchByKeyWord(String keyword) {
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');

        if(products.isEmpty()){
            throw new APIException("No products found for the keyword: " + keyword);
        }

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProductFromDb = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Product product = modelMapper.map(productDTO, Product.class);
        existingProductFromDb.setProductName(product.getProductName());
        existingProductFromDb.setProductDescription(product.getProductDescription());
        existingProductFromDb.setProductQuantity(product.getProductQuantity());
        existingProductFromDb.setProductPrice(product.getProductPrice());
        existingProductFromDb.setProductDiscount(product.getProductDiscount());
        double specialPrice =  product.getProductPrice() - ((product.getProductDiscount() * 0.01) * product.getProductPrice());
        existingProductFromDb.setSpecialPrice(specialPrice);

        Product updatedProductDTO = productRepository.save(existingProductFromDb);
        return modelMapper.map(updatedProductDTO, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product existingProductFromDb = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productRepository.delete(existingProductFromDb);
        return modelMapper.map(existingProductFromDb, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDb = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String fileName = fileService.uploadImage(path, image);

        productFromDb.setProductImage(fileName);

        Product updatedProduct = productRepository.save(productFromDb);

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }
}
