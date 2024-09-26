package com.example.progettonegozio.repositories;

import com.example.progettonegozio.entities.ProductHostedInfo;
import com.example.progettonegozio.entities.Subscription;
import com.example.progettonegozio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer>{

    List<Subscription> findSubscriptionByUser(User user);

    List<Subscription> findSubscriptionByProductHostedInfo(ProductHostedInfo productHostedInfo);

    Subscription findSubscriptionById(Integer id);

}
