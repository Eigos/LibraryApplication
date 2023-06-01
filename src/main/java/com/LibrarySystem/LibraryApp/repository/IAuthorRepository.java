package com.LibrarySystem.LibraryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.LibrarySystem.LibraryApp.model.Author;

public interface IAuthorRepository extends JpaRepository<Author, Integer>, JpaSpecificationExecutor<Author> {
    
}
