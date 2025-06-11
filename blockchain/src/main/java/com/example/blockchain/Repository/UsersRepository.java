package com.example.blockchain.Repository;


import com.example.blockchain.Domain.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, String> {

    Users findUsersByWalletAccount(@Param("wallerAccount") String account);

    Optional<Users> findByEmail(@Param("email") String email);

}
