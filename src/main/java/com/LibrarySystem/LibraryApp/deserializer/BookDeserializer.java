package com.LibrarySystem.LibraryApp.deserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.LibrarySystem.LibraryApp.model.Book;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class BookDeserializer extends StdDeserializer<Book> {

    public BookDeserializer() {
        this(null);
    }

    protected BookDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Book deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {

        JsonNode node = p.getCodec().readTree(p);

        String description = null;
        String title = null;
        String dateStr = null;
        Date date = null;
        Integer pageCount = null;
        Integer authorId = null;
        String language = null;

        if (isNodeValid(node,"description"))
            description = node.get("description").asText();

        if (isNodeValid(node, "title"))
            title = node.get("title").asText();

        // int author = node.get("author").asInt();

        if (isNodeValid(node, "date")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            dateStr = node.get("date").asText();
            date = null;
            try {
                date = format.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (isNodeValid(node, "language"))
            language = node.get("language").asText();

        if (isNodeValid(node, "pageCount"))
            pageCount = node.get("pageCount").asInt();

        if (isNodeValid(node, "author"))
            authorId = node.get("author").asInt();

        return new Book(description, title, authorId, date, language, pageCount);
    }

    private boolean isNodeValid(JsonNode node, String fieldName) {
        return node.findValue(fieldName) == null ? false : true;
    }
}
