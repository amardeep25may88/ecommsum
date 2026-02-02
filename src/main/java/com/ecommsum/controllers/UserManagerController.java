package com.ecommsum.controllers;

import com.ecommsum.dto.JwtAuthRequest;
import com.ecommsum.entity.UserEntity;
import com.ecommsum.service.JwtService;
import com.ecommsum.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserManagerController {

    @Autowired
    private UserEntityService userEntityService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/home")
    public String home(){return "home !";}

    @GetMapping("/listAll")
    public List<UserEntity> getUsers(){ return userEntityService.getAllUsers(); }

    @GetMapping("/{userId}")
    public Optional<UserEntity> getUserById(@PathVariable Long userId){ return userEntityService.getUserById(userId); }

    @PostMapping("/create")
    public UserEntity createNewUser(@RequestBody UserEntity userEntity){ return userEntityService.createNewUser(userEntity); }

    @PostMapping("/jwtauthentication")
    public String jwtAuthentication(@RequestBody JwtAuthRequest jwtAuthRequest){
        String token="";
        Authentication authentication = authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(
               jwtAuthRequest.getUserName(),jwtAuthRequest.getPassword()
        ));
        if(authentication.isAuthenticated())
            return token="JWT_Token ::: => "+jwtService.generateJwtToken(jwtAuthRequest.getUserName());
        else
            return token="JWT_Token ::: => [error in creating token]";
    }



}
