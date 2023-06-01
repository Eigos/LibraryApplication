package com.LibrarySystem.LibraryApp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.LibrarySystem.LibraryApp.model.User;
import com.LibrarySystem.LibraryApp.repository.IUserRepository;

@Service
public class UserService {

    @Autowired
    IUserRepository userRepository;

    public void addUser(User user){
        userRepository.save(user);
    }

    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }

    public void setUser(Long id, User user){
        Optional<User> userOptional = getUser(id);

        if(userOptional.isPresent()){
            throw new IllegalStateException("Unable to find user");
        }

        userRepository.save(userOptional.get());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getUserListWithSpecification(Specification<User> specification, PageRequest pageRequest) {
        return userRepository.findAll(specification, pageRequest);
    }

    public Page<User> getUserList(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest);
    }
    

}
