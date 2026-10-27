package org.example.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

public record Wage (
        String name,
        LocalDate date,
        Duration duration
){
    public Wage {
        Objects.requireNonNull(name);
        Objects.requireNonNull(date);
        Objects.requireNonNull(duration);

        if(name.isBlank()){
            throw new IllegalArgumentException("Wage name cannot be blank");
        }

        if(duration.isNegative()){
            throw new IllegalArgumentException("Wage duration cannot be negative");
        }
    }
}
