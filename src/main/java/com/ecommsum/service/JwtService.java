package com.ecommsum.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

@Service
public class JwtService {

    private static final String SECRET="VHVlIEZlYiAwMyAwMToyNToxNiBJU1QgMjAyNg=="; //orignal val ::: "Tue Feb 03 01:25:16 IST 2026" base64 ::: VHVlIEZlYiAwMyAwMToyNToxNiBJU1QgMjAyNg==

    public  String generateJwtToken(String userName){
         return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*10))
//                 .setExpiration(new Date(System.currentTimeMillis() + 1000*60))
                .addClaims(new HashMap<>())
                .signWith(getSignedKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignedKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public Claims verifySignatureAndExtractJwtToken(String token){
        return Jwts.parser()
                .setSigningKey(getSignedKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUserName(String token){
        return verifySignatureAndExtractJwtToken(token).getSubject();
    }

    public Date getExpiration(String token){
        return verifySignatureAndExtractJwtToken(token).getExpiration();
    }

    public boolean isTokenExpired(String token){
        return  getExpiration(token).before(new Date());
    }




}
