package com.demo.email.converter;

import com.google.common.base.Strings;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;
import java.util.Set;

@Converter
public class SetToStringConverter implements AttributeConverter<Set<String>, String> {

    protected static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        return attribute == null ? null : String.join(DELIMITER, attribute);
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        if (Strings.isNullOrEmpty(dbData)) {
            return Collections.emptySet();
        }
        return Set.of(dbData.split(DELIMITER));
    }

}
