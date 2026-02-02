package com.ecommsum.service;

import com.ecommsum.entity.UserEntity;
import com.ecommsum.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserEntityService implements UserDetailsService {

    @Autowired
    UserEntityRepository userEntityRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    public List<UserEntity> getAllUsers() { return userEntityRepository.findAll(); }

    public Optional<UserEntity> getUserById(long userId) { return userEntityRepository.findById(userId); }

    public UserEntity createNewUser(UserEntity userEntity) {
       UserEntity newUser= new UserEntity();
       newUser.setUserName(userEntity.getUserName());
       newUser.setPassword(passwordEncoder.encode(userEntity.getPassword()));
       newUser.setEmail(userEntity.getEmail());
       newUser.setRole(userEntity.getRole());
       newUser.setActive(userEntity.isActive());
        return userEntityRepository.save(newUser);
    }

    public UserEntity getUserByUserName(String userName, boolean isActive){
        return userEntityRepository.findByUserNameAndIsActive(userName,isActive)
                .orElseThrow( ()-> new UsernameNotFoundException("User not found with name :: "+userName));
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserEntity userByUserName = getUserByUserName(userName,true);
        return User.builder()
                .username(userByUserName.getUserName())
//                .password(passwordEncoder.encode(userByUserName.getPassword())) //NOTE ::: **** if db has already encrepted password dont use this line
                .password(userByUserName.getPassword())
                .authorities(Collections.emptyList())
                .build();

    }
}
