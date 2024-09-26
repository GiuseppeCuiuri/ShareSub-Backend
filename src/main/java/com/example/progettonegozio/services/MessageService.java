package com.example.progettonegozio.services;


import com.example.progettonegozio.entities.*;
import com.example.progettonegozio.repositories.*;
import com.example.progettonegozio.support.exceptions.ProductNotExistsException;
import com.example.progettonegozio.support.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ProductHostedRepository productHostedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductHostedInfoRepository productHostedInfoRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, ChatRepository chatRepository, ProductHostedRepository productHostedRepository){
        this.messageRepository=messageRepository;
        this.chatRepository=chatRepository;
        this.productHostedRepository = productHostedRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly=false, isolation= Isolation.READ_COMMITTED)
    public Message createMessage(int productHostedId, String text, String email) throws UserNotFoundException {
        //get User
        User host=userRepository.findUserByEmail(email);
        if(host==null)
            throw new UserNotFoundException();
        ProductHosted productHosted = productHostedRepository.findProductHostedById(productHostedId);
        Chat chat = chatRepository.findChatByProductHosted(productHosted);
        Message message = new Message();
        message.setChat(chat);
        message.setSender(host);
        message.setContent(text);
        return messageRepository.save(message);
    }


    @Transactional(readOnly=true)
    public List<Message> getMessagesByProductHostedOrderByDateAsc(int productHostedId) throws ProductNotExistsException {
        ProductHosted productHosted = productHostedRepository.findProductHostedById(productHostedId);
        if(productHosted == null)
            throw new ProductNotExistsException();
        Chat chat = chatRepository.findChatByProductHosted(productHosted);
        return messageRepository.findMessagesByChatOrderByDateAsc(chat);
    }

    @Transactional(readOnly=false, isolation= Isolation.READ_COMMITTED)
    public void deleteMessageById(int messageId) {
        messageRepository.deleteById(messageId);
    }

    @Transactional(readOnly=true)
    public List<Message> getMessagesByProductHostedInfoOrderByDateAsc(int productHostedInfoId) throws ProductNotExistsException {
        ProductHostedInfo productHostedInfo = productHostedInfoRepository.findProductHostedInfoById(productHostedInfoId);
        ProductHosted productHosted = productHostedRepository.findProductHostedById(productHostedInfo.getProductHosted().getId());
        if(productHosted == null)
            throw new ProductNotExistsException();
        Chat chat = chatRepository.findChatByProductHosted(productHosted);
        return messageRepository.findMessagesByChatOrderByDateAsc(chat);
    }

}
