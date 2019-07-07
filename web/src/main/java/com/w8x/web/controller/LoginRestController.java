package com.w8x.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginRestController {
    @PostMapping("/loginRest")
    @ResponseBody
    public String loginRest(String username, String password) {
        if("admin".equals(username) && "123".equals(password)){
            Map<String,String> map = new HashMap<>();
            map.put("userName",username);
            map.put("token", DigestUtils.md5DigestAsHex((username+password).getBytes()));
            return "OK";
        }
        return "FALL";
    }
}
