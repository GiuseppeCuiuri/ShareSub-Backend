package com.example.progettonegozio.controllers.rest;


import com.example.progettonegozio.entities.User;
import com.example.progettonegozio.services.AccountingService;
import com.example.progettonegozio.support.ResponseMessage;
import com.example.progettonegozio.support.exceptions.AmountNotAvailableException;
import com.example.progettonegozio.support.exceptions.MailUserAlreadyExistsExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/users")
public class AccountingController {

    @Autowired
    private AccountingService accountingService;

        /*
        La persona che invoca il metodo si è autenticata attraverso keycloak.
        Pertanto, controllo se ha i permessi necessari e restituisco i dati dell'utente dal database, se è presente.
        Se l'utente non esiste, lo creo e poi restituisco i suoi dati.
     */


    //@PreAuthorize("hasAuthority('app-admin')")
    @GetMapping("/getAll")
    public ResponseEntity getAll(){
        List<User> result=accountingService.getAll();
        if(result.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage("No result"),HttpStatus.OK);
        }

        return new ResponseEntity<>(new ResponseMessage("All users",result),HttpStatus.OK);

    }

    @PostMapping("/createUser")
    public ResponseEntity createUser(@RequestBody(required = true) User user){
        try {
            User result=accountingService.registerUser(user);
            return new ResponseEntity(new ResponseMessage("Added successful!",result),HttpStatus.OK);
        }catch (MailUserAlreadyExistsExceptions e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "", e);
        }
    }

    @PostMapping("/registerUserByString")
    public ResponseEntity registerUserByString(@RequestParam String email,
                                               @RequestParam String first_name,
                                               @RequestParam String last_name){
        try {
            User result=accountingService.registerUserByEmailFirstAndLastName(email,first_name,last_name);
            return new ResponseEntity(new ResponseMessage("Added successful!",result),HttpStatus.OK);
        }catch (MailUserAlreadyExistsExceptions e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "", e);
        }
    }

    @PostMapping("/redeemBalance")
    public ResponseEntity redeemBalance(@RequestParam String email,
                                        @RequestParam double amount){
        try {
            User update=accountingService.redeemBalance(email, amount);
            return new ResponseEntity(new ResponseMessage("Redeem successful!",update),HttpStatus.OK);
        }catch (AmountNotAvailableException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "", e);
        }
    }

    @GetMapping("/findByEmail")
    public ResponseEntity findByEmail(@RequestParam String email){
        User result=accountingService.findByEmail(email);
        if(result==null){
            return new ResponseEntity<>(new ResponseMessage("No result"),HttpStatus.OK);
        }
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/getBalance")
    public ResponseEntity getBalance(@RequestParam String email){
        double result=accountingService.getBalance(email);
        if(result==-1){
            return new ResponseEntity<>(new ResponseMessage("No result"),HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage("Balance",result),HttpStatus.OK);
    }


}
