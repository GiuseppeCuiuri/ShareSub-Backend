package com.example.progettonegozio.services;

import com.example.progettonegozio.entities.*;
import com.example.progettonegozio.repositories.ChatRepository;
import com.example.progettonegozio.repositories.ProductHostedRepository;
import com.example.progettonegozio.repositories.ProductRepository;
import com.example.progettonegozio.repositories.UserRepository;
import com.example.progettonegozio.support.exceptions.MaxUsersLimitExceededException;
import com.example.progettonegozio.support.exceptions.ProductAlreadyExistsExceptions;
import com.example.progettonegozio.support.exceptions.ProductNotExistsException;
import com.example.progettonegozio.support.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductHostedService {
    @Autowired
    private ProductHostedRepository productHostedRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    public ProductHostedService(ProductHostedRepository productHostedRepository, UserRepository userRepository){
        this.productHostedRepository=productHostedRepository;
        this.userRepository=userRepository;
    }

    @Transactional(readOnly=false, isolation= Isolation.READ_COMMITTED)
    public String addProductHosted(int productId, String email, int subUsers, String loginInfo, String password) throws UserNotFoundException, MaxUsersLimitExceededException {
        User host = userRepository.findUserByEmail(email);
        if (host == null)
            throw new UserNotFoundException();

        // Carica l'entità Product dal database se necessario
        Product managedProduct = entityManager.find(Product.class, productId);
        if (managedProduct == null) {
            throw new EntityNotFoundException("Product not found");
        }

        if(managedProduct.getMaxUsers() < subUsers)
            throw new MaxUsersLimitExceededException();

        entityManager.refresh(managedProduct);
        entityManager.lock(managedProduct, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        ProductHosted result = new ProductHosted();
        result.setProductAssociated(managedProduct);
        result.setHostedBy(host);
        result.setSubAvailable(true);
        result.setSubUsers(subUsers);
        result = entityManager.merge(result);

        entityManager.refresh(result);
        entityManager.lock(result, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        ProductHostedInfo productHostedInfo = new ProductHostedInfo();
        productHostedInfo.setProductHosted(result);
        productHostedInfo.setLoginUserInformation(loginInfo);
        productHostedInfo.setLoginPasswordInformation(password);
        productHostedInfo = entityManager.merge(productHostedInfo);

        Chat chat = new Chat();
        chat.setProductHosted(result);
        System.out.println("New Chat: "+chat);
        chatRepository.save(chat);

        return "Results: " + result + " " + productHostedInfo;
    }


    @Transactional(readOnly = true)
    public List<ProductHosted> showAllProducts(){
        List<ProductHosted> ret=new ArrayList<>();
        List<ProductHosted> phs=  productHostedRepository.findAll();
        for(ProductHosted ph:phs){
            if(ph.isSubAvailable())
                ret.add(ph);
        }
        return ret;
    }

    @Transactional(readOnly = true)
    public List<ProductHosted> showProductHostedByProduct(Product p){
        return productHostedRepository.findProductHostedByProductAssociated(p);
    }

    @Transactional(readOnly = true)
    public List<ProductHosted> showProductHostedByHostedBy(String email) throws UserNotFoundException {
        User host=userRepository.findUserByEmail(email);
        if(host==null)
            throw new UserNotFoundException();
        return productHostedRepository.findProductHostedByHostedBy(host);
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public ProductHosted changeSubAvailable(ProductHosted productHosted){
        ProductHosted productManager = entityManager.find(ProductHosted.class, productHosted.getId());
        entityManager.refresh(productManager);
        entityManager.lock(productManager, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        productHosted.setSubAvailable(!productHosted.isSubAvailable());
        return productHosted;
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public ProductHosted changeDate(int day, int month, int year, int id){
        ProductHosted productManager = entityManager.find(ProductHosted.class, id);
        entityManager.refresh(productManager);
        entityManager.lock(productManager, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        Date newDate= new Date(year-1900,month,day);
        System.out.println("Nuova data: "+newDate);
        productManager.setLastRenewDate(newDate);
        return productManager;
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public ProductHosted changeContinueToHost(int id){
        ProductHosted productManager = entityManager.find(ProductHosted.class, id);
        entityManager.refresh(productManager);
        entityManager.lock(productManager, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        productManager.setContinueToHost(!productManager.isContinueToHost());
        return productManager;
    }

    @Transactional(readOnly = true)
    public ProductHosted searchProductHostedById(int id) throws ProductNotExistsException {
        ProductHosted result=productHostedRepository.findProductHostedById(id);
        if(result==null){
            throw new ProductNotExistsException();
        }
        return result;
    }


}
