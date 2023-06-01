package com.LibrarySystem.LibraryApp.serializer;

import java.io.IOException;

import com.LibrarySystem.LibraryApp.model.Author;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;


public class AuthorSerializerBasic extends StdSerializer<Author> {

    public AuthorSerializerBasic() {
        this(null);
    }

    public AuthorSerializerBasic(Class<Author> src) {
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
        gen.writeEndObject();
    }

}
