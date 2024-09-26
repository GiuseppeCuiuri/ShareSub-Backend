package com.example.progettonegozio.repositories;


import com.example.progettonegozio.entities.Product;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findProductById(int id);

    Boolean existsById(int id);

    //New Metods
    List<Product> findProductsByNameContaining(String name);

    List<Product> findProductByPricePerUserLessThan(Double price);

    List<Product> findProductByType(Product.Type type);

    List<Product> findProductByNameContainingAndType(String name, Product.Type type);

    List<Product> findProductByPricePerUserLessThanAndType(Double price, Product.Type type);

    List<Product> findProductByNameContainingAndPricePerUserLessThan(String name, Double price);

    List<Product> findProductByNameContainingAndPricePerUserLessThanAndType(String name, Double price, Product.Type type);


}
