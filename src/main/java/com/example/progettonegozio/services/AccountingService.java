package com.example.progettonegozio.services;

import com.example.progettonegozio.repositories.UserRepository;
import com.example.progettonegozio.entities.User;
import com.example.progettonegozio.support.exceptions.AmountNotAvailableException;
import com.example.progettonegozio.support.exceptions.MailUserAlreadyExistsExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountingService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public AccountingService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Transactional(readOnly=false, propagation= Propagation.REQUIRED) //impostiamo propagation poiche andiamo a registrare un nuovo cliente
    public User registerUser(User user) throws MailUserAlreadyExistsExceptions{
        if(userRepository.existsByEmail(user.getEmail())){
            throw new MailUserAlreadyExistsExceptions();
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = false, isolation= Isolation.READ_COMMITTED) //impostiamo isolation poiche andiamo a leggere i dati
    public User addAndgetUser(String email,String first_name,String last_name){
        User user=userRepository.findUserByEmail(email);
        if(user==null){
            user=new User(email,first_name,last_name);
            user=userRepository.save(user);
        }
        return user;
    }

    @Transactional(readOnly = false, isolation= Isolation.READ_COMMITTED)
    public User registerUserByEmailFirstAndLastName(String email, String first_name, String last_name) throws MailUserAlreadyExistsExceptions {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(first_name);
        user.setLastName(last_name);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAll(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = false, isolation= Isolation.READ_COMMITTED)
    public User redeemBalance(String email, double amount) throws AmountNotAvailableException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            return null;
        }
        if(amount < 0) {
            throw new IllegalArgumentException();
        }
        if(user.getBalance() < amount) {
            throw new AmountNotAvailableException();
        }

        user.setBalance(user.getBalance() - amount);
        return userRepository.save(user);
    }

    @Transactional(readOnly = false, isolation= Isolation.READ_COMMITTED)
    public User findByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

     @Transactional(readOnly = true)
    public double getBalance(String email){
        User user = userRepository.findUserByEmail(email);
        if(user == null) {
            return -1;
        }
        return user.getBalance();
    }


}
