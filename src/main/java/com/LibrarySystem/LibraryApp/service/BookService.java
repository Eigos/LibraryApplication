package com.LibrarySystem.LibraryApp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.LibrarySystem.LibraryApp.model.Book;
import com.LibrarySystem.LibraryApp.repository.IBookRepository;



@Service
public class BookService {
    
    @Autowired
    private IBookRepository bookRepository;
    
    public void addBook(Book book){
        bookRepository.save(book);
    }

    public void setBook(Book book){
        bookRepository.save(book);
    }

    public List<Book> getBookList(){
        return bookRepository.findAll();
    }

    public Page<Book> getBookList(PageRequest pageRequest){
        return bookRepository.findAll(pageRequest);
    }

    public Optional<Book> getBookById(int id){
        return bookRepository.findById(id);
    }

    public Long getBookCount(){
        return bookRepository.count();
    }

    public void associateBookToAuthor(int bookId, int authorId){
        bookRepository.setAuthorIdByBookId(bookId, authorId);
    }
    
    public List<Book> getBookListWithSpecification(Specification<Book> specs){
        return bookRepository.findAll(Specification.where(specs));
    }

    public Page<Book> getBookListWithSpecification(Specification<Book> specs, PageRequest pageRequest){
        return bookRepository.findAll(Specification.where(specs), pageRequest);
    }

    public void deleteBook(Integer book_id) {
        bookRepository.deleteById(book_id);
    }

    public void updateBook(Book book) throws IllegalArgumentException {
        Optional<Book> savedBookOptional = getBookById(book.getBookId());

        if(!savedBookOptional.isPresent()){
            throw new IllegalArgumentException("Unable to find book with given id: " + book.getBookId());
        }

        Book savedBook = savedBookOptional.get();

        if(book.getAuthor() != null){
            savedBook.setAuthor(book.getAuthor());
        }

        if(book.getDate() != null){
            savedBook.setDate(book.getDate());
        }

        if(book.getDescription() != null){
            savedBook.setDescription(book.getDescription());
        }

        if(book.getTitle() != null){
            savedBook.setTitle(book.getTitle());
        }

        if(book.getLanguage() != null){
            savedBook.setLanguage(book.getLanguage());
        }

        if(book.getPageCount() != null){
            savedBook.setPageCount(book.getPageCount());
        }

        bookRepository.save(savedBook);
    }
}
