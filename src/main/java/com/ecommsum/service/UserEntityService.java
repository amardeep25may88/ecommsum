package com.ecommsum.service;

import com.ecommsum.entity.UserEntity;
import com.ecommsum.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserEntityService {

    @Autowired
    UserEntityRepository userEntityRepository;


    public List<UserEntity> getAllUsers() { return userEntityRepository.findAll(); }

    public Optional<UserEntity> getUserById(long userId) { return userEntityRepository.findById(userId); }

    public UserEntity createNewUser(UserEntity userEntity) { return userEntityRepository.save(userEntity); }


}
