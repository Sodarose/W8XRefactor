package com.w8x.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/webhook/")
public class WebHookRest {
    @Autowired
    HttpServletRequest request;

    @PostMapping("/Payload")
    public String webHookSolve(){
        String payload = request.getParameter("Payload");
        return "{'code':'1'}";
    }


}
