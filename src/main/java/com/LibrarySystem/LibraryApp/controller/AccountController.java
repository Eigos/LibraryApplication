package com.LibrarySystem.LibraryApp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LibrarySystem.LibraryApp.model.AccountDetails;
import com.LibrarySystem.LibraryApp.model.User;
import com.LibrarySystem.LibraryApp.service.AccountDetailsService;
import com.LibrarySystem.LibraryApp.service.UserService;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

@RestController
@RequestMapping(path = "/account")
public class AccountController {
    
    @Autowired
    AccountDetailsService accountDetailsService;

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public List<AccountDetails> accountList() {
        return accountDetailsService.getAllAccounts();
    }

    @RequestMapping(method = RequestMethod.POST)
    public int createAccount(@RequestParam("username") String username, @RequestParam("password") String password,
            @RequestParam("roles") String roles) {

        AccountDetails newAccount = new AccountDetails(username, password, roles);

        accountDetailsService.addAccount(newAccount);

        return HTTPResponse.SC_CREATED;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public int associateUserToAccount(@RequestParam("account_id") Long accountIndex,
            @RequestParam("user_id") Long userIndex) {

        Optional<User> user = userService.getUser(userIndex);

        if (!user.isPresent()) {
            return HTTPResponse.SC_BAD_REQUEST;
        }

        accountDetailsService.associateToUser(accountIndex, user.get());

        return HTTPResponse.SC_OK;
    }
}
