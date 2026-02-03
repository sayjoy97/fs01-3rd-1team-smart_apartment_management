package com.jjld.global.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import io.jsonwebtoken.security.Keys;

public class TokenTest {
    public static void main(String[] args) throws InterruptedException{
        String secretkey = "springsecurity-jwt-jjld-final-project-apartment-management";

        byte[] datas = secretkey.getBytes(StandardCharsets.UTF_8);
        System.out.println(datas);

        String encodingSecretKey = Base64.getEncoder().encodeToString(datas);
        System.out.println(encodingSecretKey);

    }
}
