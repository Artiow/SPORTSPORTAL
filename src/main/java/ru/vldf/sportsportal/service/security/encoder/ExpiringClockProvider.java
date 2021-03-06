package ru.vldf.sportsportal.service.security.encoder;

import org.springframework.data.util.Pair;
import ru.vldf.sportsportal.service.security.model.ExpirationType;

import java.util.Date;

/**
 * @author Namednev Artem
 */
public interface ExpiringClockProvider {

    Pair<Date, Date> gen(ExpirationType type);
}
