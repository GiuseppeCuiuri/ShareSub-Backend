package com.example.progettonegozio.controllers.rest;


import com.example.progettonegozio.entities.Product;
import com.example.progettonegozio.entities.SubPurchase;
import com.example.progettonegozio.services.ProductService;
import com.example.progettonegozio.services.SubPurchaseService;
import com.example.progettonegozio.support.ResponseMessage;
import com.example.progettonegozio.support.exceptions.ProductNotExistsException;
import com.example.progettonegozio.support.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/purchasing")
public class SubPurchaseController {

    @Autowired
    private SubPurchaseService subPurchaseService;

    @Autowired
    private ProductService productService;

    //@PreAuthorize("hasAuthority('app-user')")
    @PostMapping("/create")
    public ResponseEntity createSubPurchase(@RequestBody int productId,
                                            @RequestParam String email, Authentication autentication) {
        try{
            Product product= productService.searchProductById(productId);
            SubPurchase result=subPurchaseService.addSubPurchase(product, autentication.getName());
            return new ResponseEntity(new ResponseMessage(
                    "SubPurchase order has been processed!",result)
                    , HttpStatus.OK);
        }catch (UserNotFoundException | ProductNotExistsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage(),e);
        }
    }//createPurchase


    @GetMapping("/by_buyer")
    public ResponseEntity getPurchaseByBuyer(@RequestParam String email) {

        try {
            List<SubPurchase> result = subPurchaseService.searchSubPurchaseByBuyer(email);
            Collections.reverse(result);
            if (result.isEmpty()) {
                return new ResponseEntity(new ResponseMessage("No result!",result), HttpStatus.OK);
            }
            return new ResponseEntity(new ResponseMessage("Purchases founded",result),HttpStatus.OK);

        }catch(UserNotFoundException  e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage(),e);
        }
    }//getPurchaseInPeriodOfASingleUser

    @GetMapping("/getSubPurchase")
    public ResponseEntity getSubPurchase() {
        List<SubPurchase> result = subPurchaseService.getSubPurchases();
        return new ResponseEntity(new ResponseMessage("All SubPurchase founded",result),HttpStatus.OK);
    }//getAllPurchase
}
