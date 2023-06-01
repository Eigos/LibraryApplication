package com.LibrarySystem.LibraryApp.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.LibrarySystem.LibraryApp.UtilsCustom;
import com.LibrarySystem.LibraryApp.model.Author;
import com.LibrarySystem.LibraryApp.service.AuthorService;
import com.LibrarySystem.LibraryApp.specifications.SearchSpecification;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

@RestController
@RequestMapping(path = "/author")
public class AuthorController {

    @Autowired
    AuthorService authorService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static final int maxPageSize = 50;

    static final String numberMatchRegex = "\\d+";

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Author>> bookList(
            @RequestParam(value = "id", required = false) List<String> idStr,
            @RequestParam(value = "name", required = false) List<String> name,
            @RequestParam(value = "middle_name", required = false) List<String> middleName,
            @RequestParam(value = "surname", required = false) List<String> surname,
            // TODO - @RequestParam(value = "date_of_birth", required = false) String
            @RequestParam(value = "basic", required = false, defaultValue = "false") String basicInformationStr,
            @RequestParam(name = "index", required = false) String indexStr,
            @RequestParam(name = "size", required = false) String sizeStr) {

        logger.debug("Author search request received.");

        List<Integer> id = new LinkedList<>();
        Integer index = 0;
        Integer size = maxPageSize / 2;
        Boolean basicInformation = false;

        logger.debug("Started converting values.");
        try {

            if (idStr != null) {
                idStr.forEach(x -> {
                    if (!x.matches(numberMatchRegex)) {
                        logger.error("Invalid id. Id argmuent can only contain digits");
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Invalid id. Id argmuent can only contain digits");
                    };
                    id.add(UtilsCustom.tryParseNumberL(x).intValue());
                });
            }

            if (indexStr != null) {
                if (!indexStr.matches(numberMatchRegex)) {
                    logger.error("Invalid index. Index argmuent can only contain digits");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid index. Index argmuent can only contain digits");
                };
                index = UtilsCustom.tryParseNumberL(indexStr).intValue();
            }

            int tmpSize = 0;
            if (sizeStr != null) {
                if (!sizeStr.matches(numberMatchRegex)) {
                    logger.error("Invalid size. Size argmuent can only contain digits");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid size. Size argmuent can only contain digits");
                };
                tmpSize = UtilsCustom.tryParseNumberL(sizeStr).intValue();
            }

            if (isPageSizeValid(tmpSize))
                size = tmpSize;

            if (basicInformationStr != null)
                basicInformation = Boolean.valueOf(basicInformationStr);
        } catch (NumberFormatException | NullPointerException e) {

            logger.error("Unable to convert values on author search request", e.getMessage());

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.debug("Values converted successfully");

        List<SearchSpecification<Author>> specificationList = new ArrayList<>();

        if (SearchSpecification.isValidToParseSearchSpecificationList(id))
            specificationList
                    .add(new SearchSpecification<Author>("authorId", id.toArray(Integer[]::new)).setPrecision(true));

        if (SearchSpecification.isValidToParseSearchSpecificationList(name))
            specificationList.add(new SearchSpecification<Author>("name", name.toArray(String[]::new)));

        if (SearchSpecification.isValidToParseSearchSpecificationList(middleName))
            specificationList.add(new SearchSpecification<Author>("middleName", middleName.toArray(String[]::new)));

        if (SearchSpecification.isValidToParseSearchSpecificationList(surname))
            specificationList.add(new SearchSpecification<Author>("lastName", surname.toArray(String[]::new)));

        PageRequest pageRequest = PageRequest.of(index, size);

        Specification<Author> specification = null;

        if (!specificationList.isEmpty()) {

            StringBuilder searchInformationParametersForLogging = new StringBuilder();

            for (SearchSpecification<Author> newSpecification : specificationList) {

                if (specification == null) {
                    specification = Specification.where(newSpecification);
                    searchInformationParametersForLogging.append(newSpecification).toString();
                    continue;
                }

                specification = specification.and(newSpecification);

                searchInformationParametersForLogging.append(newSpecification.toString());
            }

            logger.debug("Searching author with parameters of :" + searchInformationParametersForLogging.toString());
        }
        
        List<Author> authorList;
        if (specification != null)
            authorList = authorService.getAuthorListWithSpecification(specification, pageRequest).toList();
        else
            authorList = authorService.getAuthorList(pageRequest).toList();

        if (basicInformation == true)
            authorList.forEach(x -> x.setBookList(null));

        logger.info(authorList.size() + " Elements were found for given request");

        return new ResponseEntity<>(authorList, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/{author_id}")
    public ResponseEntity<Author> getAuthor(@PathVariable(required = true, value = "author_id") String authorIdStr) {

        logger.info("Author get request received with author id.");

        HttpStatus responseStatus = HttpStatus.OK;

        int authorId;

        logger.debug("Trying to convert author parameter");
        try {
            authorId = UtilsCustom.tryParseNumberL(authorIdStr).intValue();
        } catch (NumberFormatException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.debug("Conversion successful");

        logger.debug("Searching for author");
        Optional<Author> authorOptional = authorService.getAuthor(authorId);

        if (authorOptional.isPresent()) {
            logger.info("Unable to find author with id of" + authorId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid book_id");
        }

        return new ResponseEntity<Author>(authorOptional.get(), responseStatus);
    }

    @RequestMapping(method = RequestMethod.POST)
    public int addAuthor(@RequestBody @Valid Author author) {

        logger.info("Author post request received. Author information : " + author.toString());
        
        try {
            authorService.addAuthor(author);
        } catch(Exception e){
            logger.info("Unable to save author information!");
            return HTTPResponse.SC_BAD_REQUEST;
        }
        
        return HTTPResponse.SC_CREATED;
    }

    @RequestMapping(method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public int updateAuthor(@RequestBody(required = true) Author author) {

        logger.info("Path request received. Updating author : " + author.toString());
        
        try {
            authorService.updateAuthor(author);
        } catch (NotFoundException e) {
            logger.error("Author not found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author not found");
        }
        
        logger.info("Author with id(" + author.getAuthorId() + ") updated successfully");
        
        return HTTPResponse.SC_OK;
    }

    @RequestMapping(path = "/{author_id}", method = RequestMethod.PATCH)
    public int updateAuthor(@PathVariable(required = true, value = "author_id") String authorIdStr,
            @RequestBody Author author) {

        int authorId;
        
        logger.info("Path request received with id");

        try {
            if (!authorIdStr.matches(numberMatchRegex)) {
                logger.error("Invalid author id. Author id argmuent can only contain digits");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid author id. Author id argmuent can only contain digits");
            }
            authorId = UtilsCustom.tryParseNumberL(authorIdStr).intValue();
        } catch (NumberFormatException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        author.setAuthorId(authorId);
        try {
            authorService.updateAuthor(author);
        } catch (NotFoundException e) {
            logger.error("Author not found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author not found");
        }

        return HTTPResponse.SC_OK;
    }

    private boolean isPageSizeValid(Integer size) {

        if (size == null)
            return false;

        return ((size < maxPageSize + 1) && (size > 0));
    }
}
