package com.LibrarySystem.LibraryApp.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.LibrarySystem.LibraryApp.model.AccountDetails;
import com.LibrarySystem.LibraryApp.model.User;
import com.nimbusds.jose.shaded.json.JSONObject;

@RestController
public class TestResource {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showWelcomePage(){
        
        Optional<User> optionalUser = Optional.ofNullable(getLoggedInUser());

        if(!optionalUser.isPresent())
            return "welcome";

        User user = optionalUser.get();

        Map<String, String> userInformationMap = new HashMap<>();

        userInformationMap.put("Name", user.getName() );
        userInformationMap.put("MiddleName", user.getMiddleName());
        userInformationMap.put("LastName", user.getLastName());

        JSONObject jsonObject = new JSONObject(userInformationMap);

        return jsonObject.toString();
    }

    private User getLoggedInUser(){
        Object principal = SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();

        if(principal instanceof AccountDetails){
            return ((AccountDetails) principal).getUser();
        }

        return null;
    }

}
