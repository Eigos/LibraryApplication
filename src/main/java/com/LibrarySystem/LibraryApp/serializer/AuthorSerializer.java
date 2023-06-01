package com.LibrarySystem.LibraryApp.serializer;

import java.io.IOException;

import com.LibrarySystem.LibraryApp.model.Author;
import com.LibrarySystem.LibraryApp.model.Book;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class AuthorSerializer extends StdSerializer<Author> {

    AuthorSerializer() {
        this(null);
    }

    protected AuthorSerializer(Class<Author> src) {
        super(src);
    }

    @Override
    public void serialize(Author value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeStartObject();
        // Author Object
        {
            gen.writeNumberField("author_id", value.getAuthorId());
            gen.writeStringField("name", value.getName());
            gen.writeStringField("middle_name", value.getMiddleName());
            gen.writeStringField("last_name", value.getLastName());
            gen.writeStringField("date_of_birth", value.getDateOfBirth().toString());
        }

        // Book Array
        if(value.getBookList() != null)
        {
            gen.writeArrayFieldStart("book_list");

            // Book Object
            for (Book book : value.getBookList()) {
                gen.writeStartObject();
                gen.writeNumberField("book_id", book.getBookId());
                gen.writeStringField("description", book.getDescription());
                gen.writeStringField("title", book.getTitle());
                gen.writeStringField("date", book.getDate().toString());
                gen.writeStringField("language", book.getLanguage());
                gen.writeNumberField("page_count", book.getPageCount());
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }
        
        gen.writeEndObject();
    }

}
