package com.LibrarySystem.LibraryApp.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.LibrarySystem.LibraryApp.UtilsCustom;
import com.LibrarySystem.LibraryApp.model.User;
import com.LibrarySystem.LibraryApp.service.UserService;
import com.LibrarySystem.LibraryApp.specifications.SearchSpecification;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    
    @Autowired
    UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static final int maxPageSize = 50;
    static final int defaultPageSize = maxPageSize / 2;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<User>> bookList(
            @RequestParam(value = "id", required = false) List<String> idStr,
            @RequestParam(value = "name", required = false) List<String> name,
            @RequestParam(value = "middle_name", required = false) List<String> middleName,
            @RequestParam(value = "last_name", required = false) List<String> lastName,
            // TODO - @RequestParam(value = "date_of_birth", required = false) String
            @RequestParam(name = "index", required = false) String indexStr,
            @RequestParam(name = "size", required = false) String sizeStr) {

        logger.debug("User search request received.");

        List<Integer> id = new LinkedList<>();
        Integer index = 0;
        Integer size = defaultPageSize;

        try {
            
            idStr.forEach(x -> {

                if (!UtilsCustom.isNumeric(x)) {
                    logger.error("Invalid id. Id argmuent can only contain digits");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid id. Id argmuent can only contain digits");
                };

                id.add(UtilsCustom.tryParseNumberL(x).intValue());
            });

            if (indexStr != null) {
                if (!UtilsCustom.isNumeric(indexStr)) {
                    logger.error("Invalid index. Index argmuent can only contain digits");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid index. Index argmuent can only contain digits");
                };
                index = UtilsCustom.tryParseNumberL(indexStr).intValue();
            }

            int tmpSize = 0;
            if (sizeStr != null) {
                if (!sizeStr.matches(sizeStr)) {
                    logger.error("Invalid size. Size argmuent can only contain digits");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid size. Size argmuent can only contain digits");
                };
                tmpSize = UtilsCustom.tryParseNumberL(sizeStr).intValue();
            }

            if (isPageSizeValid(tmpSize))
                size = tmpSize;


        } catch (NumberFormatException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.debug("Values converted successfully");

        List<Specification<User>> specificationList = new ArrayList<>();

        if (SearchSpecification.isValidToParseSearchSpecificationList(id))
            specificationList.add(SearchSpecification.parseSearchSpecificationListNumber("id", id));

        if (SearchSpecification.isValidToParseSearchSpecificationList(name))
            specificationList.add(SearchSpecification.parseSearchSpecificationListString("name", name));

        if (SearchSpecification.isValidToParseSearchSpecificationList(middleName))
            specificationList.add(SearchSpecification.parseSearchSpecificationListString("middleName", middleName));

        if (SearchSpecification.isValidToParseSearchSpecificationList(lastName))
            specificationList.add(SearchSpecification.parseSearchSpecificationListString("lastName", lastName));

        PageRequest pageRequest = PageRequest.of(index, size);

        Specification<User> specification = null;

        if (!specificationList.isEmpty()) {

            StringBuilder searchInformationParametersForLogging = new StringBuilder();

            for (Specification<User> newSpecification : specificationList) {

                if (specification == null) {
                    specification = Specification.where(newSpecification);
                    searchInformationParametersForLogging.append(newSpecification).toString();
                }

                specification = specification.and(newSpecification);
                searchInformationParametersForLogging.append(newSpecification.toString());

            }

            logger.debug("Searching user with parameters of :" + searchInformationParametersForLogging.toString());
        }

        List<User> authorList;
        if (specification != null)
            authorList = userService.getUserListWithSpecification(specification, pageRequest).toList();
        else
            authorList = userService.getUserList(pageRequest).toList();
            
        logger.info(authorList.size() + " Elements were found for given request");

        return new ResponseEntity<>(authorList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public int createUser(@Valid @RequestBody User user) {
        
        logger.info("User post request received. User information : " + user.toString());
        
        try{
            userService.addUser(user);
        }
        catch(IllegalArgumentException e){
            logger.info("Unable to save user information!");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.info("New user registered successfully");
        return HTTPResponse.SC_OK;
    }

    private boolean isPageSizeValid(Integer size){

        if(size == null)
            return false;

        return ((size < maxPageSize + 1) && (size > 0));
    }
}
