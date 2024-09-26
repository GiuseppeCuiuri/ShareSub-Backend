package com.example.progettonegozio.controllers.rest;

import com.example.progettonegozio.entities.*;
import com.example.progettonegozio.services.*;
import com.example.progettonegozio.support.ResponseMessage;
import com.example.progettonegozio.support.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductHostedService productHostedService;

    @Autowired
    private ProductHostedInfoService productHostedInfoService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;


    //@PreAuthorize("hasAuthority('app-admin')")
    @PostMapping("crea_product")
    public ResponseEntity createProduct(@RequestBody @Valid Product product){

        try{
            Product p=productService.addProduct(product);
            return new ResponseEntity(new ResponseMessage("Added succesfully!",p),HttpStatus.OK);
        }catch (ProductAlreadyExistsExceptions e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product already exist!",e);
        }

    }

    @PostMapping("crea_productHosted")
    public ResponseEntity createProductHosted(@RequestParam(required = true) int productId,
                                             @RequestParam(required = true) String email,
                                             @RequestParam(required = true) int subUsers,
                                             @RequestParam(required = true) String loginInfo,
                                             @RequestParam(required = true) String password){
        try {
            String result = productHostedService.addProductHosted(productId, email, subUsers, loginInfo, password);
            return new ResponseEntity(new ResponseMessage("Added succesfully!", result), HttpStatus.OK);
        }catch (UserNotFoundException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not exist!",e);
        } catch (MaxUsersLimitExceededException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Maximum of users limit exceeded!",e);
        }
    }//createProductHosted

    @GetMapping("/getAll")
    public ResponseEntity getAll(){
        List<Product> result=productService.showAllProducts();
        if(result.size()==0){
            return new ResponseEntity<>(new ResponseMessage("No result"),HttpStatus.OK);
        }

        return new ResponseEntity(new ResponseMessage("Founded products", result), HttpStatus.OK);
    }

    @GetMapping("/getAllProductHosted")
    public ResponseEntity getAllProductHosted(){
        List<ProductHosted> result=productHostedService.showAllProducts();
        if(result.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage("No result"),HttpStatus.OK);
        }

        return new ResponseEntity(new ResponseMessage("Founded products", result), HttpStatus.OK);
    }


    @PutMapping("/productHostedInfo/changeLoginInfo")
    public ResponseEntity changeLoginInfo(@RequestParam(required = true) int productHostedInfoId,
                                         @RequestParam(required = true) String newLoginInfo,
                                         @RequestParam(required = true) String newLoginPassword){
        ProductHostedInfo productHostedInfo=productHostedInfoService.changeLoginInfo(productHostedInfoId, newLoginInfo, newLoginPassword);
        return new ResponseEntity<>(new ResponseMessage("Login information changed",productHostedInfo),HttpStatus.OK);
    }


    @GetMapping("/getAllProductHostedInfo")
    public ResponseEntity getAllProductHostedInfo(){
        List<ProductHostedInfo> result=productHostedInfoService.showAllProducts();
        if(result.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage("No result"),HttpStatus.OK);
        }

        return new ResponseEntity(new ResponseMessage("Founded products", result), HttpStatus.OK);
    }

    @PutMapping("/changeSubAvailable")
    public ResponseEntity changeSubAvailable(@RequestBody @Valid ProductHosted productHosted){
        ProductHosted result=productHostedService.changeSubAvailable(productHosted);
        return new ResponseEntity<>(new ResponseMessage("SubAvailable changed",result),HttpStatus.OK);
    }

    @GetMapping("/search/by_id")
    public ResponseEntity getById(@RequestParam(required = true) String id){
        System.out.println(id);
        try{
            Product result=productService.searchProductById(Integer.parseInt(id));
            return new ResponseEntity<>(new ResponseMessage("Product found",result),HttpStatus.OK);
        }catch (ProductNotExistsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product not exist!",e);
        }
    }

        @GetMapping("/search/by_id_productHosted")
    public ResponseEntity getByIdProductHosted(@RequestParam(required = true) String id){
        System.out.println(id);
        try{
            ProductHosted result=productHostedService.searchProductHostedById(Integer.parseInt(id));
            return new ResponseEntity<>(new ResponseMessage("ProductHosted found",result),HttpStatus.OK);
        }catch (ProductNotExistsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"ProductHosted not exist!",e);
        }
    }



    @GetMapping("/search/by_name")
    public ResponseEntity getProductByNameContaining(@RequestParam(required = true) String name){
        System.out.println(name);
        List<Product> result=productService.showProductsByNameContaining(name);
        if(result.isEmpty())
            return new ResponseEntity<>(new ResponseMessage("No products"),HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage("FoundendProduct",result), HttpStatus.OK);
    }

    @GetMapping("/search/pByPriceLowerThan")
    public ResponseEntity getProductByPriceLowerThan(@RequestParam(required = true) double price){
        List<Product> result=productService.showProductByPriceLowerThan(price);
        if(result.isEmpty())
            return new ResponseEntity<>(new ResponseMessage("No products"),HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage("Founded products",result), HttpStatus.OK);
    }

    @GetMapping("/search/pByType")
    public ResponseEntity getProductByType(@RequestParam(required = true) Product.Type type){
        List<Product> result=productService.showProductByType(type);
        if(result.isEmpty())
            return new ResponseEntity<>(new ResponseMessage("No products"),HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage("Founded products",result), HttpStatus.OK);
    }

    @GetMapping("/search/pByNameContainingAndType")
    public ResponseEntity getProductByNameContainingAndType(@RequestParam(required = true) String name, @RequestParam(required = true) Product.Type type){
        List<Product> result=productService.showProductByNameContainingAndType(name, type);
        if(result.isEmpty())
            return new ResponseEntity<>(new ResponseMessage("No products"),HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage("Founded products",result), HttpStatus.OK);
    }

    @GetMapping("/search/pByPriceLowerThanAndType")
    public ResponseEntity getProductByPriceLowerThanAndType(@RequestParam(required = true) double price, @RequestParam(required = true) Product.Type type){
        List<Product> result=productService.showProductByPriceLowerThanAndType(price, type);
        if(result.isEmpty())
            return new ResponseEntity<>(new ResponseMessage("No products"),HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage("Founded products",result), HttpStatus.OK);
    }


    @GetMapping("search/pByNameTypePriceLower")
    public ResponseEntity getProductByNameTypePriceLower(@RequestParam String name,
                                                         @RequestParam(required = false) Product.Type type,
                                                         @RequestParam(defaultValue = "0") double price) {
        List<Product> products;

        List<ProductHosted> temp;

        List<ProductHosted> ret=new ArrayList<>();

        System.out.println(name + " " + type + " " + price);

        if (!name.isEmpty()) {
            if (price != 0) {
                if (type!=null) {
                    products = productService.showProductByNameContainingAndPriceLowerThanAndType(name, price, type);
                } else {
                    products = productService.showProductByNameContainingAndPriceLowerThan(name, price);
                }
            } else {
                if (type!=null) {
                    products = productService.showProductByNameContainingAndType(name, type);
                } else {
                    products = productService.showProductsByNameContaining(name);
                }
            }
        } else {
            if (price != 0) {
                if (type!=null) {
                    products = productService.showProductByPriceLowerThanAndType(price, type);
                } else {
                    products = productService.showProductByPriceLowerThan(price);
                }
            } else {
                if (type!=null) {
                    products = productService.showProductByType(type);
                } else {
                    products = productService.showAllProducts();
                }
            }
        }
        for(Product p:products){
            temp=productHostedService.showProductHostedByProduct(p);
            for (ProductHosted ph:temp){
                if(ph.isSubAvailable())
                    ret.add(ph);
            }
        }

        return new ResponseEntity<>(new ResponseMessage("", ret), HttpStatus.OK);
    }//il metodo di ricerca completo su cui costruisco il filtro nel frontend



    //@PreAuthorize("hasAuthority('app-admin')")
    @PutMapping("/update_product")
    public ResponseEntity updateProduct(@RequestBody(required =  true) Product product,
                                             @RequestParam(required = true) @Positive Double price){
        try {
            Product updatedP=productService.updateProduct(product,price);
            return new ResponseEntity<>(new ResponseMessage("FinalProduct updated succesfully!",updatedP),HttpStatus.OK);
        }catch (ProductNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product not exist!",e);
        }
    }

    //@PreAuthorize("hasAuthority('app-admin')")
    @PutMapping("/delete_product")
    public ResponseEntity deleteProduct(@RequestBody @Valid Product product){
        try{
            Product removeP=productService.removeProduct(product);
            return new ResponseEntity<>(new ResponseMessage("Product removed succesfully!",removeP),HttpStatus.OK);
        }catch (ProductNotExistsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product not exist!",e);
        }
    }

    //@PreAuthorize("hasAuthority('app-admin')")
    @PutMapping("/delete_product_by_id")
    public ResponseEntity deleteProductById(@RequestParam @Valid int id){
        try{
            Product removeP=productService.removeProductById(id);
            return new ResponseEntity<>(new ResponseMessage("Product removed succesfully!",removeP),HttpStatus.OK);
        }catch (ProductNotExistsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product not exist!",e);
        }
    }

    //ProductHostedInfo Methods
    @PutMapping("/productManaged/")
    public ResponseEntity getProductHostedInfoByHostedBy(@RequestParam(required = true) String email){
        try {
            List<ProductHosted> productHosteds = productHostedService.showProductHostedByHostedBy(email);
            List<ProductHostedInfo> ret = new ArrayList<>();
            for (ProductHosted p : productHosteds) {
                ret.add(productHostedInfoService.showProductHostedByProductHosted(p));
            }
            return new ResponseEntity<>(new ResponseMessage("ProductHostedInfo founded", ret), HttpStatus.OK);
        }
        catch (UserNotFoundException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not Found",e);
        }

    }

    //Subscription Methods
    @GetMapping("/subscription/get")
    public ResponseEntity getSubscription(@RequestParam (required = true) String email) throws UserNotFoundException {
        List<Subscription> result=subscriptionService.findSubscriptionByUser(email);
        if(result.isEmpty())
            return new ResponseEntity<>(new ResponseMessage("No subscriptions"),HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage("Founded subscriptions",result), HttpStatus.OK);

    }

    @PostMapping("/subscription/addSubscription")
    public ResponseEntity addSubscription(@RequestParam (required = true) int productHostedId,
                                          @RequestParam (required = true) String email,
                                          @RequestParam (required = true) boolean payWithBalance){
        try {
            System.out.println("productHostedId: " + productHostedId + " email: " + email + " payWithBalance: " + payWithBalance);
            Subscription subscription = subscriptionService.addSubscription(productHostedId, email, payWithBalance);
            return new ResponseEntity<>(new ResponseMessage("Subscription added", subscription), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found", e);
        } catch (NotSlotAvailable e) {
            throw new RuntimeException(e);
        } catch (UserCannotBuyException  e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot buy", e);
        } catch (UserAldreadySubscribed e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already subscribed", e);
        }

    }

    @PutMapping("/subscription/renewSubscription")
    public ResponseEntity renewSubscription(@RequestParam (required = true) int id){
        Subscription subscription=subscriptionService.changeRenew(id);
        return new ResponseEntity<>(new ResponseMessage("Subscription renewed",subscription),HttpStatus.OK);
    }

    @PutMapping("/subscription/removeSubscription")
    public ResponseEntity removeSubscription(@RequestParam (required = true) int id){
        Subscription subscription=subscriptionService.removeSubscription(id);
        return new ResponseEntity<>(new ResponseMessage("Subscription removed",subscription),HttpStatus.OK);
    }

    @GetMapping("/subscription/getAll")
    public ResponseEntity getAllSubscriptions(){
        List<Subscription> result=subscriptionService.findAllSubscriptions();
        if(result.isEmpty())
            return new ResponseEntity<>(new ResponseMessage("No subscriptions"),HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage("Founded subscriptions",result), HttpStatus.OK);
    }

    @PostMapping("/subscription/updateAll")
    public ResponseEntity updateAllSubscriptions(){
        subscriptionService.updateAllSubscription();
        return new ResponseEntity<>(new ResponseMessage("Subscriptions updated"),HttpStatus.OK);
    }

    @PutMapping("/productHosted/changeDate")
    public ResponseEntity changeDate(@RequestParam(required = true) int day,
                                    @RequestParam(required = true) int month,
                                    @RequestParam(required = true) int year,
                                    @RequestParam(required = true) int id){
        ProductHosted productHosted=productHostedService.changeDate(day, month, year, id);
        return new ResponseEntity<>(new ResponseMessage("Date changed",productHosted),HttpStatus.OK);
    }


    @PutMapping("/productHosted/changeContinueToHost")
    public ResponseEntity changeContinueToHost(@RequestParam(required = true) int id){
        ProductHosted productHosted=productHostedService.changeContinueToHost(id);
        return new ResponseEntity<>(new ResponseMessage("SubAvailable changed",productHosted),HttpStatus.OK);
    }

    @GetMapping("/productHostedInfo/getByUser")
    public ResponseEntity getProductHostedInfoByUser(@RequestParam(required = true) String email){
        try {
            List<ProductHostedInfo> result = productHostedInfoService.showProductHostedInfoByUser(email);
            if (result.isEmpty())
                return new ResponseEntity<>(new ResponseMessage("Founded products", result), HttpStatus.OK);
            return new ResponseEntity<>(new ResponseMessage("Founded products", result), HttpStatus.OK);
        }catch (UserNotFoundException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not found",e);
        }
    }

    @GetMapping("/productHosted/getByProductAssociated")
    public ResponseEntity getProductHostedByProduct(@RequestBody(required = true) Product p){
        List<ProductHosted> result = productHostedService.showProductHostedByProduct(p);
        if (result.isEmpty())
            return new ResponseEntity<>(new ResponseMessage("No result"), HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage("Founded products", result), HttpStatus.OK);
    }

    //MESSAGES AND CHATS METHODS
    @PostMapping("/message/createMessage")
    public ResponseEntity createMessage(@RequestParam(required = true) int productHostedId,
                                        @RequestParam(required = true) String text,
                                        @RequestParam (required=true) String email) throws UserNotFoundException {
        return new ResponseEntity<>(new ResponseMessage("Message sent", messageService.createMessage(productHostedId, text, email)), HttpStatus.OK);
    }

    @GetMapping("/message/getMessagesByProductHosted")
    public ResponseEntity getMessagesByProductHosted(@RequestParam(required = true) int productHostedId){
        try {
            List<Message> result = messageService.getMessagesByProductHostedOrderByDateAsc(productHostedId);
            if (result.isEmpty())
                return new ResponseEntity<>(new ResponseMessage("No result"), HttpStatus.OK);
            return new ResponseEntity<>(new ResponseMessage("Founded messages", result), HttpStatus.OK);
        }catch (ProductNotExistsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product not found",e);
        }
    }

    @GetMapping("/message/getMessagesByProductHostedInfo")
    public ResponseEntity getMessagesByProductHostedInfo(@RequestParam(required = true) int productHostedInfoId){
        try {
            List<Message> result = messageService.getMessagesByProductHostedInfoOrderByDateAsc(productHostedInfoId);
            if (result.isEmpty())
                return new ResponseEntity<>(new ResponseMessage("No result"), HttpStatus.OK);
            return new ResponseEntity<>(new ResponseMessage("Founded messages", result), HttpStatus.OK);
        } catch (ProductNotExistsException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/message/deleteMessage")
    public ResponseEntity deleteMessage(@RequestParam(required = true) int messageId){
        messageService.deleteMessageById(messageId);
        return new ResponseEntity<>(new ResponseMessage("Message deleted"), HttpStatus.OK);
    }

    @GetMapping("/getAllChats")
    public ResponseEntity getAllChats(){
        List<Chat> result = chatService.getAllChats();
        if(result.isEmpty())
            return new ResponseEntity<>(new ResponseMessage("No result"), HttpStatus.OK);

        return new ResponseEntity<>(new ResponseMessage("Founded chats", result), HttpStatus.OK);
    }

    @GetMapping("/subscription/subscriptionAlreadyExists")
    public ResponseEntity subscriptionAlreadyExists(@RequestParam(required = true) int productHostedId,
                                                   @RequestParam(required = true) String email){
        boolean result=subscriptionService.subscriptionAlreadyExists(productHostedId, email);
        return new ResponseEntity<>(new ResponseMessage("Subscription already exists", result), HttpStatus.OK);
    }

    @GetMapping("/getProductPricePerUser")
    public ResponseEntity getProductPrice(@RequestParam(required = true) int productHostedId) throws ProductNotExistsException {
        double result=productService.getProductPricePerUser(productHostedId);
        return new ResponseEntity<>(new ResponseMessage("Product price", result), HttpStatus.OK);
    }

    @PostMapping("/updateProductPricePerUser")
    public ResponseEntity updateProductPrice(@RequestParam(required = true) int productHostedId,
                                             @RequestParam(required = true) double price) throws ProductNotExistsException {
        Product result=productService.updateProductPricePerUser(productHostedId, price);
        return new ResponseEntity<>(new ResponseMessage("Product price updated", result), HttpStatus.OK);
    }






}
