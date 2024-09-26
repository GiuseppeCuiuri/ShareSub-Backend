package com.example.progettonegozio.services;


import com.example.progettonegozio.entities.Product;
import com.example.progettonegozio.services.AccountingService;
import com.example.progettonegozio.entities.ProductHosted;
import com.example.progettonegozio.entities.SubPurchase;
import com.example.progettonegozio.entities.User;
import com.example.progettonegozio.repositories.SubPurchaseRepository;
import com.example.progettonegozio.repositories.UserRepository;
import com.example.progettonegozio.support.exceptions.NoPurchasesFindedExceptions;
import com.example.progettonegozio.support.exceptions.QuantityProductUnvailableException;
import com.example.progettonegozio.support.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;
import java.util.Objects;

@Service
public class SubPurchaseService {

    @Autowired
    private SubPurchaseRepository subPurchaseRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = QuantityProductUnvailableException.class)
    public SubPurchase addSubPurchase(Product product, String email) throws  UserNotFoundException {
        User user=userRepository.findUserByEmail(email);
        if(user==null)
            throw new UserNotFoundException();
        entityManager.refresh(product);
        entityManager.lock(product, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        SubPurchase result = subPurchaseRepository.save(new SubPurchase());
        result.setBuyer(user);
        result.setProduct(product);
        result.setPrice(product.getPricePerUser());

        result=entityManager.merge(result); //aggiornamento dei record nel database

        return result;

    }


    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<SubPurchase> searchSubPurchaseByBuyer(String email) throws UserNotFoundException {
        User user=userRepository.findUserByEmail(email);
        if(user==null)
            throw new UserNotFoundException();

        List<SubPurchase> result = subPurchaseRepository.findByBuyer(user);

        return result;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<SubPurchase> getSubPurchases() {
        return subPurchaseRepository.findAll();
    }

}
