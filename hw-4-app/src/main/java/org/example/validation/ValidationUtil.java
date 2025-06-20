package org.example.validation;

import org.example.annotation.MaxLength;
import org.example.annotation.NotEmpty;

import java.lang.reflect.Field;

public class ValidationUtil {

    public static void validate(Object dto) {
        Class<?> clazz = dto.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            NotEmpty ne = field.getAnnotation(NotEmpty.class);
            if (ne != null) {
                try {
                    Object value = field.get(dto);
                    if (value == null || ((String) value).isEmpty()) {
                        throw new IllegalArgumentException(
                                clazz.getSimpleName() + "." + field.getName() + ": " + ne.message()
                        );
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            MaxLength ml = field.getAnnotation(MaxLength.class);
            if (ml != null) {
                try {
                    Object value = field.get(dto);
                    if (value != null && ((String) value).length() > ml.value()) {
                        throw new IllegalArgumentException(
                                clazz.getSimpleName() + "." + field.getName() + ": " + ml.message()
                        );
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
