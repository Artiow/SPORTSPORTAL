package ru.vldf.sportsportal.dto.validation.validators;

import ru.vldf.sportsportal.dto.validation.annotations.Past;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

/**
 * @author Namednev Artem
 */
public class PastLocalDateTimeValidator implements ConstraintValidator<Past, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime ldt, ConstraintValidatorContext constraintValidatorContext) {
        if (ldt == null) return true;
        return ldt.isBefore(LocalDateTime.now());
    }
}
