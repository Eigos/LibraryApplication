package com.LibrarySystem.LibraryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.LibrarySystem.LibraryApp.model.User;


public interface IUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{
    
}
