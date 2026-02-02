package com.ecommsum.controllers;

import com.ecommsum.entity.UserEntity;
import com.ecommsum.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserManagerController {

    @Autowired
    UserEntityService userEntityService;

    @GetMapping("/home")
    public String home(){return "home !";}

    @GetMapping("/listAll")
    public List<UserEntity> getUsers(){ return userEntityService.getAllUsers(); }

    @GetMapping("/{userId}")
    public Optional<UserEntity> getUserById(@PathVariable Long userId){ return userEntityService.getUserById(userId); }

    @PostMapping("/create")
    public UserEntity createNewUser(@RequestBody UserEntity userEntity){ return userEntityService.createNewUser(userEntity); }



}
