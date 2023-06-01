package com.LibrarySystem.LibraryApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.LibrarySystem.LibraryApp.model.AccountDetails;

public interface IAccountRepository extends JpaRepository<AccountDetails, Long>{

    Optional<AccountDetails> findByUsername(String username);
    
}
