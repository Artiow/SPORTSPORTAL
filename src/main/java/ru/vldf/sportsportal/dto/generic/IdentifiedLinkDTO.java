package ru.vldf.sportsportal.dto.generic;

/**
 * @author Namednev Artem
 */
public interface IdentifiedLinkDTO extends IdentifiedDTO, LinkedDTO {

    Integer getId();

    void setId(Integer id);
}