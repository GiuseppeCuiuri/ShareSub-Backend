package com.example.progettonegozio.repositories;

import com.example.progettonegozio.entities.Product;
import com.example.progettonegozio.entities.ProductHosted;
import com.example.progettonegozio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductHostedRepository extends JpaRepository<ProductHosted, Integer>{

    List<ProductHosted> findProductHostedByProductAssociated(Product product);

    List<ProductHosted> findProductHostedByHostedBy(User host);

    ProductHosted findProductHostedById(int id);

}
