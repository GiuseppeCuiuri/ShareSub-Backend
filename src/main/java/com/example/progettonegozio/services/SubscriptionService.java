package com.example.progettonegozio.services;


import com.example.progettonegozio.entities.*;
import com.example.progettonegozio.repositories.*;
import com.example.progettonegozio.support.exceptions.NotSlotAvailable;
import com.example.progettonegozio.support.exceptions.UserAldreadySubscribed;
import com.example.progettonegozio.support.exceptions.UserCannotBuyException;
import com.example.progettonegozio.support.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    private ProductHostedRepository productHostedRepository;

    @Autowired
    private ProductHostedInfoRepository productHostedInfoRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager entityManager;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository, EntityManager entityManager, ProductHostedRepository productHostedRepository, ProductHostedInfoRepository productHostedInfoRepository, ChatRepository chatRepository, ChatService chatService){
        this.subscriptionRepository=subscriptionRepository;
        this.userRepository=userRepository;
        this.entityManager=entityManager;
        this.productHostedRepository = productHostedRepository;
        this.productHostedInfoRepository = productHostedInfoRepository;
        this.chatRepository = chatRepository;
    }

    public List<Subscription> findSubscriptionByUser(String email) throws UserNotFoundException {
        User user=userRepository.findUserByEmail(email);
        if(user==null)
            throw new UserNotFoundException();
        return subscriptionRepository.findSubscriptionByUser(user);
    }

    public List<Subscription> findSubscriptionByProductHostedInfo(ProductHostedInfo productHostedInfo) {
        return subscriptionRepository.findSubscriptionByProductHostedInfo(productHostedInfo);
    }

    public List<Subscription> findAllSubscriptions() {
        return subscriptionRepository.findAll();
    }


    @Transactional(readOnly=false, isolation= Isolation.READ_COMMITTED)
    public Subscription addSubscription(int ProductHostedId, String email, boolean payWithBalance) throws UserNotFoundException, UserCannotBuyException, UserAldreadySubscribed, NotSlotAvailable {
        User user=userRepository.findUserByEmail(email);
        System.out.println(user);
        ProductHosted productHosted=productHostedRepository.findProductHostedById(ProductHostedId);
        if(user==null)
            throw new UserNotFoundException();
        //Get the productHostedInfo from the productHosted
        if(email.equals(productHosted.getHostedBy().getEmail()))
            throw new UserCannotBuyException();
        //Check if the user is already in the subscription of the productHosted
        if(subscriptionRepository.findSubscriptionByUser(user).stream().anyMatch(subscription -> subscription.getProductHostedInfo().equals(productHostedInfoRepository.findProductHostedInfoByProductHosted(productHosted))))
            throw new UserAldreadySubscribed();
        //Check if the productHosted have available slot
        if(!productHosted.isSubAvailable())
            throw new NotSlotAvailable();

        Subscription subscription=new Subscription();

        ProductHosted managedProduct = entityManager.find(ProductHosted.class, productHosted.getId());
        if (managedProduct == null) {
            throw new EntityNotFoundException("ProductHosted not found");
        }
        entityManager.refresh(managedProduct);
        entityManager.lock(managedProduct, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        ProductHostedInfo productHostedInfo=this.productHostedInfoRepository.findProductHostedInfoByProductHosted(productHosted);
        System.out.println(productHostedInfo);
        List<Subscription> subscriptions=subscriptionRepository.findSubscriptionByProductHostedInfo(productHostedInfo);
        if(productHosted.getSubUsers()-subscriptions.size()==1) {
            managedProduct.setSubAvailable(false);
        }
        subscription.setProductHostedInfo(productHostedInfo);
        subscription.setUser(user);
        subscription = entityManager.merge(subscription);

        //Create a subPurchase
        SubPurchase subPurchase = new SubPurchase();
        subPurchase.setBuyer(user);
        subPurchase.setProduct(productHosted.getProductAssociated());
        subPurchase.setPrice(productHosted.getProductAssociated().getPricePerUser());
        entityManager.merge(subPurchase);

        //Update the productHosted
        managedProduct.setSubUsers(managedProduct.getSubUsers()-1);
        entityManager.merge(managedProduct);

        if(payWithBalance){
            if(user.getBalance() - productHosted.getProductAssociated().getPricePerUser()<0)
                throw new UserCannotBuyException();
            user.setBalance(user.getBalance() - productHosted.getProductAssociated().getPricePerUser());
            entityManager.merge(user);

        }

        User host = productHosted.getHostedBy();
        host.setBalance(host.getBalance() + productHosted.getProductAssociated().getPricePerUser());
        entityManager.merge(host);

        return subscription;

    }

    @Transactional(readOnly=false, isolation= Isolation.READ_COMMITTED)
    public void addEarningsToUser(User user, double earning){
        user.setBalance(user.getBalance()+earning);
        userRepository.save(user);
    }

    @Transactional(readOnly = false, isolation= Isolation.READ_COMMITTED)
    public void deductMoneyToUser(User user, double spending){
        user.setBalance(user.getBalance()-spending);
        userRepository.save(user);
    }


    @Transactional(readOnly=false, isolation= Isolation.READ_COMMITTED)
    public Subscription removeSubscription(int id) {
        Subscription subscriptionR=subscriptionRepository.findSubscriptionById(id);
        entityManager.lock(subscriptionR, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        subscriptionRepository.delete(subscriptionR);
        //update available slot
        ProductHosted productHosted=subscriptionR.getProductHostedInfo().getProductHosted();
        entityManager.refresh(productHosted);
        entityManager.lock(productHosted, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        if(!productHosted.isSubAvailable()){
            productHosted.setSubAvailable(true);
        }
        return subscriptionR;
    }

    @Transactional(readOnly=true)
    public boolean subscriptionAlreadyExists(int productHostedId, String email){
        User user=userRepository.findUserByEmail(email);
        List<Subscription> subscription = subscriptionRepository.findSubscriptionByUser(user);
        for (Subscription sub: subscription){
            if(sub.getProductHostedInfo().getProductHosted().getId()==productHostedId)
                return true;
        }
        return false;
    }


    @Transactional(readOnly=false, isolation= Isolation.READ_COMMITTED)
    public void updateAllSubscription() {
        //get the Date at this moment
        Date date= Date.from(java.time.Instant.now());
        //Iteration on all productHosted
        List<ProductHosted> productHosteds = productHostedRepository.findAll();
        //iterate on all productHosted
        for(ProductHosted productHosted: productHosteds){
            //if productHosted need to renew the subscriptions
            if(productHosted.getLastRenewDate().getTime()+ (long) productHosted.getProductAssociated().getRenewRate() *24*60*60*1000<=date.getTime()){
                //if the productHosted not continue to host
                if(!productHosted.isContinueToHost()){
                    //delete all the subscriptions linked to the productHosted
                    List<Subscription> subscriptions = subscriptionRepository.findSubscriptionByProductHostedInfo(productHostedInfoRepository.findProductHostedInfoByProductHosted(productHosted));
                    for(Subscription subscription: subscriptions){
                        entityManager.lock(subscription, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                        subscriptionRepository.delete(subscription);
                    }
                    //delete the productHostedInfo associated to the productHosted
                    ProductHostedInfo productHostedInfo = productHostedInfoRepository.findProductHostedInfoByProductHosted(productHosted);
                    entityManager.lock(productHostedInfo, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                    productHostedInfoRepository.delete(productHostedInfo);

                    Chat chat = chatRepository.findChatByProductHosted(productHosted);
                    //delete the chat and all the messages associated to the productHosted
                    List<Message> messages = messageRepository.findMessagesByChatOrderByDateAsc(chat);
                    for(Message message: messages){
                        entityManager.lock(message, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                        messageRepository.delete(message);
                    }
                    entityManager.lock(chat, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                    chatRepository.delete(chat);

                    //delete the productHosted
                    entityManager.lock(productHosted, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                    productHostedRepository.delete(productHosted);
                }
                else{
                    //update the lastRenewDate
                    entityManager.refresh(productHosted);
                    entityManager.lock(productHosted, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                    productHosted.setLastRenewDate(date);

                    //iterate on all subscriptions linked to the productHosted
                    List<Subscription> subscriptions = subscriptionRepository.findSubscriptionByProductHostedInfo(productHostedInfoRepository.findProductHostedInfoByProductHosted(productHosted));
                    for(Subscription subscription: subscriptions){
                        int usersToDelete= 0;
                        if(!subscription.isRenew()){
                            //delete the subscription
                            entityManager.lock(subscription, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                            subscriptionRepository.delete(subscription);
                            usersToDelete++;
                        }
                        else{
                            //create a new subPurchase for each subscription
                            SubPurchase newSubPurchase = new SubPurchase();
                            newSubPurchase.setBuyer(subscription.getUser());
                            newSubPurchase.setProduct(productHosted.getProductAssociated());
                            newSubPurchase.setPrice(productHosted.getProductAssociated().getPricePerUser());
                            entityManager.merge(newSubPurchase);

                        }
                        if(usersToDelete>0){
                            entityManager.refresh(productHosted);
                            entityManager.lock(productHosted, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                            productHosted.setSubUsers(productHosted.getSubUsers()-usersToDelete);
                        }
                    }
                }

            }
        }
    }

    @Transactional(readOnly=false, isolation= Isolation.READ_COMMITTED)
    public Subscription changeRenew(int id) {
        Subscription subscriptionR=subscriptionRepository.findSubscriptionById(id);
        entityManager.refresh(subscriptionR);
        entityManager.lock(subscriptionR, javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        boolean newRenew = !subscriptionR.isRenew();
        subscriptionR.setRenew(newRenew);
        return subscriptionR;
    }



}
