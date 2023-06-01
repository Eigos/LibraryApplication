package com.LibrarySystem.LibraryApp.serializer;

import java.io.IOException;

import com.LibrarySystem.LibraryApp.model.Book;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;


public class BookSerializer extends StdSerializer<Book> {

    public BookSerializer() {
        this(null);
    }

    protected BookSerializer(Class<Book> t) {
        super(t);
    }

    @Override
    public void serialize(Book value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeStartObject();

        // Book Object
        {
            gen.writeNumberField("book_id", value.getBookId());
            gen.writeStringField("description", value.getDescription());
            gen.writeStringField("title", value.getTitle());
            gen.writeStringField("date", value.getDate().toString());
            gen.writeStringField("language", value.getLanguage());
            gen.writeNumberField("page_count", value.getPageCount());
        }

        // Author Object
        {
            gen.writeFieldName("author");
            gen.writeStartObject();
            gen.writeNumberField("author_id", value.getAuthor().getAuthorId());
            gen.writeStringField("name", value.getAuthor().getName());
            gen.writeStringField("middle_name", value.getAuthor().getMiddleName());
            gen.writeStringField("last_name", value.getAuthor().getLastName());
            gen.writeStringField("date_of_birth", value.getAuthor().getDateOfBirth().toString());

            gen.writeEndObject();
        }

        gen.writeEndObject();
    }

}
