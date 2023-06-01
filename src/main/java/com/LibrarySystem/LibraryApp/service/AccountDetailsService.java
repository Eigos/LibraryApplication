package com.LibrarySystem.LibraryApp.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.LibrarySystem.LibraryApp.model.AccountDetails;
import com.LibrarySystem.LibraryApp.model.User;
import com.LibrarySystem.LibraryApp.repository.IAccountRepository;

@Service
public class AccountDetailsService implements UserDetailsService {
    
    @Autowired
    IAccountRepository accountRepository;

    public AccountDetailsService() {}

    @Override 
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<AccountDetails> accountOptional = accountRepository.findByUsername(username); 

        accountOptional.orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));

        AccountDetails accountDetails = accountOptional.get();

        return accountDetails;
    }

    public void addAccount(AccountDetails accountDetails){
        accountRepository.save(accountDetails);
    }

    public void associateToUser(Long accountId, User user){

        Optional<AccountDetails> accountDetailsOptional = getAccountDetails(accountId);

        if(!accountDetailsOptional.isPresent()){
            throw new IllegalStateException("Unable to find AccountDetails");
        }

        AccountDetails accountDetails = accountDetailsOptional.get();

        accountDetails.setUserId(user.getId());

        setAccountDetails(accountDetails);
    }

    public Optional<AccountDetails> getAccountDetails(Long id){
        return accountRepository.findById(id);
    }

    public void setAccountDetails(AccountDetails accountDetails){

        accountRepository.save(accountDetails);
    }

    public List<AccountDetails> getAllAccounts(){
        return accountRepository.findAll();
    }
}
