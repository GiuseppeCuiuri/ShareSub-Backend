package com.example.progettonegozio.repositories;

import com.example.progettonegozio.entities.SubPurchase;
import com.example.progettonegozio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubPurchaseRepository extends JpaRepository<SubPurchase, Integer> {

    List<SubPurchase> findByBuyer(User user);


}
