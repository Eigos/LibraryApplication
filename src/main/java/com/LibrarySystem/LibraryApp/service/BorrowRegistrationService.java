package com.LibrarySystem.LibraryApp.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.LibrarySystem.LibraryApp.model.Book;
import com.LibrarySystem.LibraryApp.model.BorrowRegistration;
import com.LibrarySystem.LibraryApp.repository.IBorrowRegistrationRepository;
import com.LibrarySystem.LibraryApp.specifications.SearchSpecification;

@Service
public class BorrowRegistrationService {
    @Autowired
    private IBorrowRegistrationRepository registrationRepository;

    @Autowired
    private BookService bookService;

    public void addRegistration(BorrowRegistration registration){
        
        if(doesBookExists(registration.getBookId()))
            return;

        registrationRepository.save(registration);
    }

    public boolean tryBorrowBook(Integer accountToBind, Integer bookToBorrow){

        if(!isBookAvailable(bookToBorrow))
            return false;

        BorrowRegistration newRegistration = new BorrowRegistration();

        newRegistration.setBookId(bookToBorrow);
        newRegistration.setAccountId(accountToBind);
        newRegistration.setReturnDate(null);

        java.util.Date localDate = null;
        try {
            localDate = new SimpleDateFormat("yyyy-MM-dd").parse(LocalDate.now().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        newRegistration.setBorrowDate(localDate);

        registrationRepository.save(newRegistration);

        return true;
    }

    public List<BorrowRegistration> findAll(){
        return registrationRepository.findAll();
    }

    public List<BorrowRegistration> getRegistrationListWithSpecifications(Specification<BorrowRegistration> specification){
        return registrationRepository.findAll(specification);
    }

    private boolean isBookAvailable(Integer bookId){

        if(!doesBookExists(bookId))
            return false;

        Specification<BorrowRegistration> specBook = new SearchSpecification<BorrowRegistration>("bookId", bookId).setPrecision(true);

        List<BorrowRegistration> registrations = registrationRepository.findAll(Specification.where(specBook));

        for(BorrowRegistration registration : registrations){
            if(registration.getReturnDate() == null)
                return false;
        }

        return true;
    }

    private boolean doesBookExists(Integer bookId){

        Optional<Book> registrations = bookService.getBookById(bookId);

        if(registrations.isPresent())
            return true;

        return false;
    }

    public void setReturnDate(BorrowRegistration registration){

        java.util.Date localDate = null;
        try {
            localDate = new SimpleDateFormat("yyyy-MM-dd").parse(LocalDate.now().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        registration.setReturnDate(localDate);
    }

    public List<BorrowRegistration> findRegistrationByBookId(Integer bookId){

        Specification<BorrowRegistration> specification = Specification.where(new SearchSpecification<BorrowRegistration>("bookId", bookId));

        return registrationRepository.findAll(specification);
    }

    public List<BorrowRegistration> findRegistrationByAccountId(Integer accountId){
        Specification<BorrowRegistration> specification = Specification.where(new SearchSpecification<BorrowRegistration>("accountId", accountId));

        return registrationRepository.findAll(specification);
    }

    public Optional<BorrowRegistration> findRegistrationById(Integer registrationId){
        return registrationRepository.findById(registrationId);
    }

    
}
