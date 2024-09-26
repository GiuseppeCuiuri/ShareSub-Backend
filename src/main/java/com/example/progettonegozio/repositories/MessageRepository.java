package com.example.progettonegozio.repositories;

import com.example.progettonegozio.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.progettonegozio.entities.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findMessagesByChatOrderByDateAsc(Chat chat);



}
