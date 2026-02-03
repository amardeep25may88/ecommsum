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
    public ResponseEntity<String> home(){ return ResponseEntity.ok("home"); }

    @GetMapping("/listAll")
    public ResponseEntity<List<UserEntity>> getUsers(){ return ResponseEntity.ok(userEntityService.getAllUsers()); }

    @GetMapping("/{userId}")
    public ResponseEntity<Optional<UserEntity>>getUserById(@PathVariable Long userId){ return ResponseEntity.ok(userEntityService.getUserById(userId)); }

    @PostMapping("/create")
    public UserEntity createNewUser(@RequestBody UserEntity userEntity){ return userEntityService.createNewUser(userEntity); }

    @PostMapping("/jwtauthentication")
    public ResponseEntity<String> jwtAuthentication(@RequestBody JwtAuthRequest jwtAuthRequest){
        String token="";
        Authentication authentication = authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(
               jwtAuthRequest.getUserName(),jwtAuthRequest.getPassword()
        ));
        if(authentication.isAuthenticated()) {
            token = "JWT_Token ::: => " + jwtService.generateJwtToken(jwtAuthRequest.getUserName());
            return ResponseEntity.ok(token);
        }else {
            token = "JWT_Token ::: => [error in creating token]";
            return ResponseEntity.ok(token);
        }
    }



}
