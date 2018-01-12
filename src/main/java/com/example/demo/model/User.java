package com.example.demo.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by song on 2017/10/23.
 */
@Data
public class User {
    @Data
    class Permissions{
        String role = "admin";
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Value("${web.userName}")
    String username;
    @Value("${web.passWord}")
    String password;
    Permissions Permissions;
}
