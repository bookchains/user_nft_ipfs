package com.example.blockchain.Services;

import com.example.blockchain.Domain.Entity.Users;
import com.example.blockchain.Repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;


    public Users registerUser(Users user){

        return usersRepository.save(user);
    }

    public Users findUsersByAccount(String account){

        return usersRepository.findUsersByWalletAccount(account);
    }
}
