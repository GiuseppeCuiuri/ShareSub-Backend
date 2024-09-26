package com.example.progettonegozio.services;

import com.example.progettonegozio.entities.Product;
import com.example.progettonegozio.entities.ProductHosted;
import com.example.progettonegozio.entities.ProductHostedInfo;
import com.example.progettonegozio.entities.User;
import com.example.progettonegozio.repositories.ProductHostedInfoRepository;
import com.example.progettonegozio.repositories.ProductHostedRepository;
import com.example.progettonegozio.repositories.UserRepository;
import com.example.progettonegozio.support.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductHostedInfoService {

    @Autowired
    private final ProductHostedInfoRepository productHostedInfoRepository;
    @Autowired
    private UserRepository userRepository;
     @Autowired
    private ProductHostedRepository productHostedRepository;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    public ProductHostedInfoService(ProductHostedInfoRepository productHostedInfoRepository, UserRepository userRepository, ProductHostedRepository productHostedRepository, EntityManager entityManager) {
        this.productHostedInfoRepository = productHostedInfoRepository;
        this.userRepository = userRepository;
        this.productHostedRepository = productHostedRepository;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<ProductHostedInfo> showAllProducts(){
        return productHostedInfoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ProductHostedInfo showProductHostedByProductHosted(ProductHosted p){
        return productHostedInfoRepository.findProductHostedInfoByProductHosted(p);
    }

    @Transactional(readOnly = true)
    public List<ProductHostedInfo> showProductHostedInfoByUser(String email) throws UserNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if (user == null)
            throw new UserNotFoundException();

        List<ProductHosted> productHostedByHostedBy = productHostedRepository.findProductHostedByHostedBy(user);
        if(productHostedByHostedBy.isEmpty())
            return null;

        List<ProductHostedInfo> productHostedInfoList = new ArrayList<>();

        for(ProductHosted productHosted : productHostedByHostedBy){
            ProductHostedInfo productHostedInfo = productHostedInfoRepository.findProductHostedInfoByProductHosted(productHosted);
            productHostedInfoList.add(productHostedInfo);
        }
        return productHostedInfoList;
    }

    //create a method that make able to change login information
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public ProductHostedInfo changeLoginInfo(int productHostedInfoId, String newLoginInfo, String newLoginPassword) {
        ProductHostedInfo productHostedInfo = productHostedInfoRepository.findById(productHostedInfoId).get();
        productHostedInfo.setLoginUserInformation(newLoginInfo);
        productHostedInfo.setLoginPasswordInformation(newLoginPassword);
        return productHostedInfoRepository.save(productHostedInfo);
    }



}