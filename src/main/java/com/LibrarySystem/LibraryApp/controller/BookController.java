package com.LibrarySystem.LibraryApp.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.LibrarySystem.LibraryApp.UtilsCustom;
import com.LibrarySystem.LibraryApp.model.Book;
import com.LibrarySystem.LibraryApp.service.AuthorService;
import com.LibrarySystem.LibraryApp.service.BookService;
import com.LibrarySystem.LibraryApp.specifications.SearchSpecification;

@RestController
@RequestMapping(path = "/book")
public class BookController {

    @Autowired
    BookService bookService;

    @Autowired
    AuthorService authorService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static final int maxPageSize = 50;
    static final int defaultPageSize = maxPageSize / 2;

    static final String numberMatchRegex = "\\d+";

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Book>> bookList(
            @RequestParam(name = "id", required = false) List<String> idStr,
            @RequestParam(name = "description", required = false) List<String> description,
            @RequestParam(name = "title", required = false) List<String> title,
            // TODO - @RequestParam(name = "date", required = false) Long dateTS,
            @RequestParam(name = "language", required = false) List<String> language,
            @RequestParam(name = "page_count", required = false) List<String> pageCountStr,
            @RequestParam(name = "index", required = false) String indexStr,
            @RequestParam(name = "size", required = false) String sizeStr) {

        logger.debug("Book search request received.");

        List<Integer> id = new LinkedList<>();
        List<Integer> pageCount = new LinkedList<>();
        Integer index = 0;
        Integer size = defaultPageSize;

        logger.debug("Started converting values.");

        try {
            if (idStr != null)
            idStr.forEach(x -> {
                
                if (!UtilsCustom.isNumeric(x)) {
                    logger.error("Invalid id. Id argmuent can only contain digits");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid id. Id argmuent can only contain digits");
                };

                id.add(UtilsCustom.tryParseNumberL(x).intValue());
            });

            if (pageCountStr != null)
            
            pageCountStr.forEach(x -> {

                if (!UtilsCustom.isNumeric(x)) {
                    logger.error("Invalid Page Count. Page Count argmuent can only contain digits");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid Page Count. Page Count argmuent can only contain digits");
                };

                pageCount.add(UtilsCustom.tryParseNumberL(x).intValue());
                
            });
                
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


        } catch (NumberFormatException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.debug("Values converted successfully");

        List<Specification<Book>> specificationList = new ArrayList<>();

        if (SearchSpecification.isValidToParseSearchSpecificationList(id))
            specificationList.add(SearchSpecification.parseSearchSpecificationListNumber("bookId", id));

        if (SearchSpecification.isValidToParseSearchSpecificationList(title))
            specificationList.add(SearchSpecification.parseSearchSpecificationListString("title", title));

        if (SearchSpecification.isValidToParseSearchSpecificationList(description))
            specificationList.add(SearchSpecification.parseSearchSpecificationListString("description", description));

        if (SearchSpecification.isValidToParseSearchSpecificationList(language))
            specificationList.add(SearchSpecification.parseSearchSpecificationListString("language", language));

        if (SearchSpecification.isValidToParseSearchSpecificationList(pageCount))
            specificationList.add(SearchSpecification.parseSearchSpecificationListNumber("pageCount", pageCount));

        PageRequest pageRequest = PageRequest.of(index, size);

        Specification<Book> specification = null;

        if (!specificationList.isEmpty()) {

            StringBuilder searchInformationParametersForLogging = new StringBuilder();

            for (Specification<Book> newSpecification : specificationList) {

                if (specification == null) {
                    specification = Specification.where(newSpecification);
                    searchInformationParametersForLogging.append(newSpecification).toString();
                }

                specification = specification.and(newSpecification);

                searchInformationParametersForLogging.append(newSpecification.toString());

            }

            logger.debug("Searching author with parameters of :" + searchInformationParametersForLogging.toString());
        }

        Page<Book> bookList;
        if (specification != null)
            bookList = bookService.getBookListWithSpecification(specification, pageRequest);
        else
            bookList = bookService.getBookList(pageRequest);

        logger.info(bookList.getTotalElements() + " Elements were found for given request");

        return new ResponseEntity<>(bookList.toList(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{book_id}", method = RequestMethod.GET)
    public ResponseEntity<Book> getBookById(
            @PathVariable(name = "book_id", required = true) String bookIdStr) {

        logger.info("Book get request received with author id.");

        int bookId;

        logger.debug("Trying to convert book parameter");

        try {
            bookId = UtilsCustom.tryParseNumberL(bookIdStr).intValue();
        } catch (NumberFormatException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        logger.debug("Conversion successful");
        
        logger.debug("Searching for book");
        Optional<Book> bookOptional = bookService.getBookById(bookId);

        if (!bookOptional.isPresent()){
            logger.info("Unable to find book with id of" + bookId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid book_id");
        }

        logger.debug("Book with id " + bookOptional.get().getBookId() + " found");

        return new ResponseEntity<Book>(bookOptional.get(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void bookAdd(@RequestBody @Valid Book book) {

        logger.info("Author post request received. Author information : " + book.toString());

        try {
            if (book.getAuthorId() != null) {
                book.setAuthor(authorService.getAuthor(book.getAuthorId()).get());
            }

            bookService.addBook(book);
            logger.info("New book added.");

        } catch(Exception e){
            logger.info("Unable to save book information!");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to save book!");
        }

    }

    @RequestMapping(value = "/{book_id}", method = RequestMethod.PATCH)
    public void bookUpdate(@PathVariable(name = "book_id", required = true) String bookIdStr, @RequestBody Book book) {

        logger.info("Path request received. Updating book : " + book.toString());

        int bookId;

        logger.debug("Trying to convert book parameter");

        try {
            bookId = UtilsCustom.tryParseNumberL(bookIdStr).intValue();
        } catch (NumberFormatException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.debug("Conversion successful");

        try{
            book.setBookId(bookId);
            bookService.updateBook(book);
        } catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.info("Book with id(" + book.getBookId() + ") updated successfully");
    }

    @RequestMapping(value = "/{book_id}", method = RequestMethod.DELETE)
    public void bookDelete(@PathVariable(name = "book_id", required = true) String bookIdStr) {

        logger.info("Delete request received.");

        int bookId;

        logger.debug("Trying to convert book parameter");
        try {
            bookId = UtilsCustom.tryParseNumberL(bookIdStr).intValue();
        } catch (NumberFormatException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        logger.debug("Conversion successful");

        try{
            bookService.deleteBook(bookId);
            logger.debug("Book with id " + bookId + " deleted.");
        }catch(IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to find book.");
        }

    }
    @RequestMapping(value = "/pages")
    public ResponseEntity<Integer> getPageCount(
            @RequestParam(value = "page_size", required = false) String pageSizeStr) {

        logger.info("Page Size requested.");

        int pageSize = defaultPageSize;

        logger.debug("Trying to convert parameter");

        if (pageSizeStr != null) {
            try {
                pageSize = UtilsCustom.tryParseNumberL(pageSizeStr).intValue();
            } catch (NumberFormatException | NullPointerException e) {
                logger.debug("Unable to convert parameter");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        logger.debug("Conversion successful");

        int pageCount = (int) (bookService.getBookCount().intValue() / pageSize);

        return new ResponseEntity<Integer>(pageCount, HttpStatus.OK);
    }

    private boolean isPageSizeValid(Integer size){

        if(size == null)
            return false;

        return ((size < maxPageSize + 1) && (size > 0));
    }
}
