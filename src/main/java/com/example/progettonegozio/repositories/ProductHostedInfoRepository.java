package com.example.progettonegozio.repositories;

import com.example.progettonegozio.entities.ProductHosted;
import com.example.progettonegozio.entities.ProductHostedInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProductHostedInfoRepository extends JpaRepository<ProductHostedInfo, Integer>{

    ProductHostedInfo findProductHostedInfoByProductHosted(ProductHosted productHosted);

    ProductHostedInfo findProductHostedInfoById(int id);

}
