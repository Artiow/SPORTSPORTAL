package ru.vldf.sportsportal.dto.general;

/**
 * @author Namednev Artem
 */
public interface WordbookDTO extends IdentifiedDTO {

    String getCode();

    void setCode(String code);

    String getName();

    void setName(String name);
}
