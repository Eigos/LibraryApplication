package com.LibrarySystem.LibraryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.LibrarySystem.LibraryApp.model.Book;

public interface IBookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book>{

    @Transactional(propagation = Propagation.REQUIRED)
    @Modifying
    @Query(value = "update book_table set book_table.author_id = :authorId where book_table.book_id = :bookId", nativeQuery = true)
    void setAuthorIdByBookId(@Param("bookId") Integer bookId, @Param("authorId") Integer authorId);

}
