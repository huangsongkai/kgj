package com.example.demo.controller;

import com.example.demo.model.LoginInDTO;
import com.example.demo.model.User;
import com.example.demo.model.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by song on 2017/1110.
 */
@RestController
@RequestMapping("/api/v1")
public class UserController{
    @PostMapping("/user/login")
    public Response login(@RequestBody UserDTO userDTO, HttpServletRequest request, HttpServletResponse response) {
        System.out.println();
        if (userDTO.getPassword().equals("admin") && userDTO.getUsername().equals("admin")) {
            Cookie cookie = new Cookie("token", "{id:1, deadline:"+System.currentTimeMillis()+"}");
            cookie.setMaxAge(900000);
            response.addCookie(cookie);
            return new Response("200");
        }else
            return new Response("500");
    }

    @GetMapping("/user")
    public Object check(@CookieValue("token") String token) {
        if(token != null){
            return new LoginInDTO(true, new User());
        }
        return new Response("200");
    }


    @Data
    @AllArgsConstructor
    class Response{
        String status;
    }

}
