package com.example.progettonegozio.services;

import com.example.progettonegozio.entities.Product;
import com.example.progettonegozio.repositories.ProductHostedRepository;
import com.example.progettonegozio.repositories.ProductRepository;
import com.example.progettonegozio.support.exceptions.ProductAlreadyExistsExceptions;
import com.example.progettonegozio.support.exceptions.ProductNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ProductHostedRepository productHostedRepository;
    @Autowired
    private ProductHostedService productHostedService;

    @Autowired
    public ProductService(ProductRepository productRepository, EntityManager entityManager){
        this.productRepository=productRepository;
        this.entityManager=entityManager;
    }

    @Transactional(readOnly=false, isolation= Isolation.READ_COMMITTED)
    public Product addProduct(Product product) throws ProductAlreadyExistsExceptions{
        if(productRepository.existsById(product.getId())){
            throw new ProductAlreadyExistsExceptions();
        }
        System.out.println(product);
        return productRepository.save(product);
    }


    @Transactional(readOnly = true)
    public Product searchProductById(int id) throws ProductNotExistsException {
        Product result=productRepository.findProductById(id);
        if(result==null){
            throw new ProductNotExistsException();
        }
        return result;
    }



    @Transactional(readOnly = true)
    public Product updateProduct(Product product, Double price) throws ProductNotExistsException {
        if(price==null)
            throw new RuntimeException("Quantity or/and price must not be null");
        Product productR=productRepository.findProductById(product.getId());
        if(productR==null)
            throw new ProductNotExistsException();
        entityManager.lock(productR, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        productR.setPrice(price);
        return productR;
    }

    @Transactional(readOnly = true)
    public List<Product> showAllProducts(){
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> showProductsByNameContaining(String name){
        return productRepository.findProductsByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public List<Product> showProductByPriceLowerThan(Double price){
        return productRepository.findProductByPricePerUserLessThan(price);
    }

    @Transactional(readOnly = true)
    public List<Product> showProductByType(Product.Type type){
        return productRepository.findProductByType(type);
    }

    @Transactional(readOnly = true)
    public List<Product> showProductByNameContainingAndType(String name, Product.Type type){
        return productRepository.findProductByNameContainingAndType(name, type);
    }

    @Transactional(readOnly = true)
    public List<Product> showProductByPriceLowerThanAndType(Double price, Product.Type type){
        return productRepository.findProductByPricePerUserLessThanAndType(price, type);
    }

    @Transactional(readOnly = true)
    public List<Product> showProductByNameContainingAndPriceLowerThan(String name, Double price){
        return productRepository.findProductByNameContainingAndPricePerUserLessThan(name, price);
    }

    @Transactional(readOnly = true)
    public List<Product> showProductByNameContainingAndPriceLowerThanAndType(String name, Double price, Product.Type type){
        return productRepository.findProductByNameContainingAndPricePerUserLessThanAndType(name, price, type);
    }


    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public Product removeProduct(Product product) throws ProductNotExistsException {
        Product productR=productRepository.findProductById(product.getId());
        if(productR==null)
            throw new ProductNotExistsException();
        entityManager.lock(productR, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        productRepository.delete(productR);
        return productR;
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public Product removeProductById(int id) throws ProductNotExistsException {
        Product productR=productRepository.findProductById(id);
        if(productR==null)
            throw new ProductNotExistsException();
        entityManager.lock(productR, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        productRepository.delete(productR);
        return productR;
    }

    @Transactional(readOnly = true)
    public double getProductPricePerUser(int id) throws ProductNotExistsException {
        Product product = productHostedService.searchProductHostedById(id).getProductAssociated();
        if(product == null)
            throw new ProductNotExistsException();
        return product.getPricePerUser();
    }

    //TODO: Quando si aggiorna il productPrice

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public Product updateProductPricePerUser(int id, double price) throws ProductNotExistsException {
        Product product = productHostedService.searchProductHostedById(id).getProductAssociated();
        if(product == null)
            throw new ProductNotExistsException();
        entityManager.lock(product, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        product.setPricePerUser(price);
        return product;
    }

}
