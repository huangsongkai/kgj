package com.example.demo.controller;

import com.example.demo.model.LoginInDTO;
import com.example.demo.model.User;
import com.example.demo.model.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Response login(@RequestParam UserDTO userDTO, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        if (userDTO.getPassword().equals(user.getPassword()) && userDTO.getUsername().equals(user.getUsername())) {
            LoginInDTO  loginInDTO = new LoginInDTO();
            loginInDTO.setSuccess(true);
            loginInDTO.setUser(user);
            Cookie cookie = new Cookie("token","token");
            cookie.setMaxAge(900000);
            response.addCookie(cookie);
            return new Response("200");
        }else
            return new Response("500");
    }

    @Data
    @AllArgsConstructor
    class Response{
        String status;
    }

}
