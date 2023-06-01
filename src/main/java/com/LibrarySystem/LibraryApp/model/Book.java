package com.LibrarySystem.LibraryApp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.LibrarySystem.LibraryApp.deserializer.BookDeserializer;
import com.LibrarySystem.LibraryApp.serializer.BookSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonDeserialize(using =  BookDeserializer.class)
@JsonSerialize(using = BookSerializer.class)
@Entity(name = "book_table")
@Table(name = "book_table")
public class Book implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "book_id")
    private Integer bookId;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = true)
    private Author author;

    @Transient
    @Column(name = "author_id", nullable = true)
    private Integer authorId;

    public Integer getAuthorId(){
        return authorId;
    }

    public void setAuthorId(int authorId){
        this.authorId = authorId; 
    }

    @Column(name = "date")
    private Date date;

    @Column(name = "language")
    private String language;

    @Column(name = "page_count")
    private Integer pageCount;
    
    @OneToMany(mappedBy = "bookId", fetch = FetchType.LAZY)
    private Set<BorrowRegistration> borrowRegistrations;


    public Book(String description, String title, Date date, String language, Integer pageCount) {
        this.description = description;
        this.title = title;
        this.date = date;
        this.language = language;
        this.pageCount = pageCount;
    }
    public Book(String description, String title, Author author, Date date, String language, Integer pageCount) {
        this.description = description;
        this.title = title;
        this.author = author;
        this.date = date;
        this.language = language;
        this.pageCount = pageCount;
    }

    public Book(String description, String title, Integer authorId, Date date, String language, Integer pageCount) {
        this.description = description;
        this.title = title;
        this.authorId = authorId;
        this.date = date;
        this.language = language;
        this.pageCount = pageCount;
    }

    public Book() {
    }

    public Author getAuthor(){
        return author;
    }
    public void setAuthor(Author author){
        this.author = author;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public Integer getPageCount() {
        return pageCount;
    }
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bookId == null) ? 0 : bookId.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((author == null) ? 0 : author.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((language == null) ? 0 : language.hashCode());
        result = prime * result + ((pageCount == null) ? 0 : pageCount.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Book other = (Book) obj;
        if (bookId == null) {
            if (other.bookId != null)
                return false;
        } else if (!bookId.equals(other.bookId))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (author == null) {
            if (other.author != null)
                return false;
        } else if (!author.equals(other.author))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (language == null) {
            if (other.language != null)
                return false;
        } else if (!language.equals(other.language))
            return false;
        if (pageCount == null) {
            if (other.pageCount != null)
                return false;
        } else if (!pageCount.equals(other.pageCount))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Book [bookId=" + getBookId() + ", description=" + getDescription() + ", title=" + getTitle() + ", author=" + getAuthor().hashCode()
                + ", date=" + date + ", language=" + language + ", pageCount=" + pageCount + "]";
    }


}
