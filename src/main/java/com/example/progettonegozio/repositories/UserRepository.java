package com.example.progettonegozio.repositories;

import com.example.progettonegozio.entities.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserByEmail(String email);
    boolean existsByEmail(String email);


}

