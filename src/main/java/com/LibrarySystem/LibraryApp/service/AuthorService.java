package com.LibrarySystem.LibraryApp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.LibrarySystem.LibraryApp.model.Author;
import com.LibrarySystem.LibraryApp.repository.IAuthorRepository;

@Service
public class AuthorService {

    @Autowired
    private IAuthorRepository authorRepository;

    public void addAuthor(Author author) {
        authorRepository.save(author);
    }

    public void setAuthor(Author author) {
        authorRepository.save(author);
    }

    public List<Author> getAuthorList() {
        return authorRepository.findAll();
    }

    public Page<Author> getAuthorList(PageRequest pageRequest) {
        return authorRepository.findAll(pageRequest);
    }

    public Optional<Author> getAuthor(int id) {
        return authorRepository.findById(id);
    }

    public boolean doesExist(Author newAuthor) {
        for (Author author : authorRepository.findAll()) {
            if (author.hashCode() == newAuthor.hashCode())
                return true;
        }

        return false;
    }

    public List<Author> getAuthorListWithSpecification(Specification<Author> specs) {
        return authorRepository.findAll(Specification.where(specs));
    }

    public Page<Author> getAuthorListWithSpecification(Specification<Author> specification, PageRequest pageRequest) {
        return authorRepository.findAll(Specification.where(specification), pageRequest);
    }

    public void updateAuthor(Author author) throws NotFoundException {
        Optional<Author> savedAuthorOptional = getAuthor(author.getAuthorId());

        if(!savedAuthorOptional.isPresent()){
            throw new NotFoundException(); // Unable to retrieve author from database
        }

        Author savedAuthor = savedAuthorOptional.get();

        if(author.getName() != null){
            savedAuthor.setName(author.getName());
        }

        if(author.getMiddleName() != null){
            savedAuthor.setMiddleName(author.getMiddleName());
        }

        if(author.getLastName() != null){
            savedAuthor.setLastName(author.getLastName());
        }

        if(author.getDateOfBirth() != null){
            savedAuthor.setDateOfBirth(author.getDateOfBirth());
        }

        authorRepository.save(savedAuthor);
    }
}
