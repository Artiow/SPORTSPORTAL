package ru.vldf.sportsportal.dto.sectional.lease;

import lombok.Getter;
import lombok.Setter;
import ru.vldf.sportsportal.dto.generic.VersionedDTO;
import ru.vldf.sportsportal.dto.sectional.common.specialized.UserLinkDTO;
import ru.vldf.sportsportal.dto.sectional.lease.specialized.ReservationResumeDTO;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Namednev Artem
 */
@Getter
@Setter
public class OrderDTO implements VersionedDTO {

    @NotNull(groups = IdCheck.class)
    @Min(value = 1, groups = IdCheck.class)
    private Integer id;

    @NotNull(groups = VersionCheck.class)
    @Min(value = 0, groups = VersionCheck.class)
    private Long version;

    @NotNull(groups = FieldCheck.class)
    @Min(value = 0, groups = FieldCheck.class)
    @Digits(integer = 6, fraction = 2, groups = FieldCheck.class)
    private BigDecimal sum;

    @Null(groups = FieldCheck.class)
    private URI paymentLink;

    @NotNull(groups = FieldCheck.class)
    private LocalDateTime datetime;

    @NotNull(groups = FieldCheck.class)
    private LocalDateTime expiration;

    @NotNull(groups = FieldCheck.class)
    private Boolean isPaid;

    @NotNull(groups = FieldCheck.class)
    private Boolean isOwnerOccupied;

    @Valid
    @NotNull(groups = FieldCheck.class)
    private UserLinkDTO customer;

    @Valid
    @NotEmpty(groups = FieldCheck.class)
    private List<ReservationResumeDTO> reservations;


    public interface IdCheck extends VersionCheck {

    }

    public interface CreateCheck extends FieldCheck {

    }

    public interface UpdateCheck extends VersionCheck, FieldCheck {

    }

    private interface VersionCheck {

    }

    private interface FieldCheck {

    }
}
