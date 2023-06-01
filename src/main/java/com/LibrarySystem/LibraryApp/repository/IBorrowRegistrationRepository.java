package com.LibrarySystem.LibraryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.LibrarySystem.LibraryApp.model.BorrowRegistration;

public interface IBorrowRegistrationRepository extends JpaRepository<BorrowRegistration, Integer>, JpaSpecificationExecutor<BorrowRegistration>  {
    
}
