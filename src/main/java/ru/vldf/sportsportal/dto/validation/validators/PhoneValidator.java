package ru.vldf.sportsportal.dto.validation.validators;

import ru.vldf.sportsportal.dto.validation.annotations.Phone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Namednev Artem
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) return true;
        return s.matches("^(\\+7\\()?[0-9]{3}\\)?[0-9]{3}-?[0-9]{2}-?[0-9]{2}$");
    }
}
