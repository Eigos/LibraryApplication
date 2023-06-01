package com.LibrarySystem.LibraryApp.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.LibrarySystem.LibraryApp.UtilsCustom;
import com.LibrarySystem.LibraryApp.model.BorrowRegistration;
import com.LibrarySystem.LibraryApp.service.BorrowRegistrationService;
import com.LibrarySystem.LibraryApp.specifications.SearchSpecification;

@RestController
@RequestMapping("borrow_registration")
public class BorrowRegistrationController {

    @Autowired
    BorrowRegistrationService borrowRegistrationService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BorrowRegistration>> borrowRegistrationList(
            @RequestParam(name = "id", required = false) List<String> idStr,
            @RequestParam(name = "book_id", required = false) List<String> bookIdStr,
            @RequestParam(name = "account_id", required = false) List<String> accountIdStr
    // TODO - @RequestParam(name = "date", required = false) Long dateTS,
    // TOOD - @RequestParam(name = "return_date", required = false) List<Date>
    // returnDate,
    ) {

        logger.debug("Borrow Registration search request received.");

        List<Integer> id = new LinkedList<>();
        List<Integer> bookId = new LinkedList<>();
        List<Integer> accountId = new LinkedList<>();

        logger.debug("Started converting values.");

        try {
            
            idStr.forEach(x -> {

                if (!UtilsCustom.isNumeric(x)) {
                    logger.error("Invalid id. Id argmuent can only contain digits");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid id. Id argmuent can only contain digits");
                };

                id.add(UtilsCustom.tryParseNumberL(x).intValue());
            });

            bookIdStr.forEach(x -> {

                if (!UtilsCustom.isNumeric(x)) {
                    logger.error("Invalid Book Id. Book Id argmuent can only contain digits");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid Book Id. Book Id argmuent can only contain digits");
                };

                bookId.add(UtilsCustom.tryParseNumberL(x).intValue());
            });

            accountIdStr.forEach(x ->  {

                if (!UtilsCustom.isNumeric(x)) {
                    logger.error("Invalid Account Id. Account Id argmuent can only contain digits");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid Account Id. Account Id argmuent can only contain digits");
                };

                accountId.add(UtilsCustom.tryParseNumberL(x).intValue());
            });

        } catch (NumberFormatException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.debug("Values converted successfully");

        List<Specification<BorrowRegistration>> specificationList = new ArrayList<>();

        if (SearchSpecification.isValidToParseSearchSpecificationList(id))
            specificationList.add(SearchSpecification.parseSearchSpecificationListNumber("id", id));

        if (SearchSpecification.isValidToParseSearchSpecificationList(bookId))
            specificationList.add(SearchSpecification.parseSearchSpecificationListNumber("bookId", bookId));

        if (SearchSpecification.isValidToParseSearchSpecificationList(accountId))
            specificationList.add(SearchSpecification.parseSearchSpecificationListNumber("accountId", accountId));

        Specification<BorrowRegistration> specification = null;

        if (!specificationList.isEmpty()) {

            StringBuilder searchInformationParametersForLogging = new StringBuilder();

            for (Specification<BorrowRegistration> newSpecification : specificationList) {

                if (specification == null) {
                    specification = Specification.where(newSpecification);
                    searchInformationParametersForLogging.append(newSpecification).toString();
                }

                specification = specification.and(newSpecification);
            }

            logger.debug("Searching author with parameters of :" + searchInformationParametersForLogging.toString());
        }

        return new ResponseEntity<>(borrowRegistrationService.getRegistrationListWithSpecifications(specification),
                HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void borrowBook(@RequestParam(value = "book_id") String bookIdStr,
            @RequestParam(value = "account_id") String accountIdStr) {


        Integer bookId;
        Integer accountId;

        try {
            bookId = UtilsCustom.tryParseNumberL(bookIdStr).intValue();
            accountId = UtilsCustom.tryParseNumberL(accountIdStr).intValue();
        } catch (NumberFormatException | NullPointerException e) {
            logger.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        borrowRegistrationService.tryBorrowBook(accountId, bookId);
    }

    @RequestMapping(value = "/{registration_id}", method = RequestMethod.GET)
    public ResponseEntity<BorrowRegistration> getBookRegistrations(
            @PathVariable(value = "registration_id", required = true) String registrationIdStr) {

        logger.info("Registration get request received with registration id.");

        Integer registrationId = null;

        logger.debug("Trying to convert registration parameter");

        try {
            registrationId = UtilsCustom.tryParseNumberL(registrationIdStr).intValue();
        } catch (NumberFormatException | NullPointerException e) {
            logger.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.debug("Conversion successful");
        
        logger.debug("Searching for registration");

        Optional<BorrowRegistration> registrationOptional = borrowRegistrationService
                .findRegistrationById(registrationId);

        if (!registrationOptional.isPresent()) {
            logger.info("Unable to find registration with id: " + registrationId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find registration with id: " + registrationId);
        }

        logger.info("Registration with id " + registrationId + " found");

        return new ResponseEntity<>(registrationOptional.get(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{registration_id}", method = RequestMethod.POST)
    public ResponseEntity<BorrowRegistration> setReturnDate(
            @PathVariable(value = "registration_id", required = true) String registrationIdStr) {

        logger.info("Registration post request received with registration id.");

        Integer registrationId = null;
        
        logger.debug("Trying to convert registration parameter");
        
        try {
            registrationId = UtilsCustom.tryParseNumberL(registrationIdStr).intValue();

        } catch (NumberFormatException | NullPointerException e) {
            logger.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.debug("Conversion successful");
        
        logger.debug("Searching for registration");

        Optional<BorrowRegistration> registrationOptional = borrowRegistrationService
                .findRegistrationById(registrationId);

        if (!registrationOptional.isPresent()) {
            logger.info("Unable to find registration with id: " + registrationId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find registration with id: " + registrationId);
        }

        borrowRegistrationService.setReturnDate(registrationOptional.get());

        logger.info("Registration closed with current time");

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
