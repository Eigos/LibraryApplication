package com.LibrarySystem.LibraryApp.deserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.LibrarySystem.LibraryApp.model.Author;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class AuthorDeserializer extends StdDeserializer<Author> {

    public AuthorDeserializer() {
        this(null);
    }

    protected AuthorDeserializer(Class<?> src) {
        super(src);
    }

    @Override
    public Author deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        Integer id = null;
        String name = null;
        String middleName = null;
        String lastName = null;
        String dateStr = null;
        Date date = null;

        if (isNodeValid(node, "author_id"))
            id = node.get("author_id").asInt();

        if (isNodeValid(node, "name"))
            name = node.get("name").asText();

        if (isNodeValid(node, "middle_name"))
            middleName = node.get("middle_name").asText();

        if (isNodeValid(node, "last_name"))
            lastName = node.get("last_name").asText();

        if (isNodeValid(node, "date_of_birth")) {
            dateStr = node.get("date_of_birth").asText();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            // TO-DO : handle properly
            try {
                date = format.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Author newAuthor = new Author(id, name, middleName, lastName, date);

        return newAuthor;
    }

    private boolean isNodeValid(JsonNode node, String fieldName) {
        return node.findValue(fieldName) == null ? false : true;
    }

}
