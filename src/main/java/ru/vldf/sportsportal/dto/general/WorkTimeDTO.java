package ru.vldf.sportsportal.dto.general;

import ru.vldf.sportsportal.dto.general.root.DataTransferObject;

import java.time.LocalTime;

/**
 * @author Namednev Artem
 */
public interface WorkTimeDTO extends DataTransferObject {

    LocalTime getOpening();

    void setOpening(LocalTime opening);

    LocalTime getClosing();

    void setClosing(LocalTime closing);

    Boolean getHalfHourAvailable();

    void setHalfHourAvailable(Boolean halfHourAvailable);

    Boolean getFullHourRequired();

    void setFullHourRequired(Boolean fullHourRequired);
}
