package com.ecommsum;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

public class TestBugs {
    public static void main(String[] args) {

        String val=new Date(System.currentTimeMillis() + 1000*60*30).toString();
        System.out.println(val);


//        PasswordEncoder encoder = new BCryptPasswordEncoder();
//        System.out.println(encoder.encode("amar123"));
//        System.out.println(encoder.matches("amar123",
//                "$2a$10$lBGMcGE/g1lH7yNndBB4XOHeG5Ik4JeWuVDQa/3Bzd9ia/0H7ae06"));

    }





}
