package com.demo.email.converter;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.demo.email.converter.SetToStringConverter.DELIMITER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

class SetToStringConverterTest {

    private final String delimetedString = "abc" + DELIMITER + "def" + DELIMITER + "ghi";
    private final Set<String> stringSet = new LinkedHashSet<>(List.of("abc", "def", "ghi"));

    private SetToStringConverter underTest = new SetToStringConverter();

    @Test
    public void shouldConvertToDatabaseColumnTest() {
        Set<String> input = stringSet;

        String result = underTest.convertToDatabaseColumn(input);

        assertEquals(delimetedString, result);
    }

    @Test
    public void shouldConvertToDatabaseColumnWhenNullTest() {
        Set<String> input = null;

        String result = underTest.convertToDatabaseColumn(input);

        assertNull(result);
    }

    @Test
    public void shouldConvertToEntityAttributeTest() {
        String input = delimetedString;

        Set<String> result = underTest.convertToEntityAttribute(input);

        assertThat(result, hasSize(3));
        assertThat(result, containsInAnyOrder("abc", "def", "ghi"));
    }

    @Test
    public void shouldConvertToEntityAttributeWhenNullTest() {
        String input = null;

        Set<String> result = underTest.convertToEntityAttribute(input);

        assertEquals(Collections.emptySet(), result);
    }

}