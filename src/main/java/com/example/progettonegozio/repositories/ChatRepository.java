package com.example.progettonegozio.repositories;

import com.example.progettonegozio.entities.Chat;
import com.example.progettonegozio.entities.ProductHosted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    Chat findChatByProductHosted(ProductHosted productHostedId);

}
