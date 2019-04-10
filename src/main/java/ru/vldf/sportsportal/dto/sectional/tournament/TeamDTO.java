package ru.vldf.sportsportal.dto.sectional.tournament;

import lombok.Getter;
import lombok.Setter;
import ru.vldf.sportsportal.dto.generic.LinkedDTO;
import ru.vldf.sportsportal.dto.generic.VersionedDTO;
import ru.vldf.sportsportal.dto.sectional.common.specialized.PictureLinkDTO;
import ru.vldf.sportsportal.dto.sectional.common.specialized.UserLinkDTO;

import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * @author Namednev Artem
 */
@Getter
@Setter
public class TeamDTO implements VersionedDTO {

    @Null(groups = FieldCheck.class)
    @NotNull(groups = IdCheck.class)
    @Min(value = 1, groups = IdCheck.class)
    private Integer id;

    @NotNull(groups = VersionCheck.class)
    @Min(value = 0, groups = VersionCheck.class)
    private Long version;

    @NotBlank(groups = FieldCheck.class)
    @Size(min = 4, max = 45, groups = FieldCheck.class)
    private String name;

    private Boolean isLocked;

    private Boolean isDisabled;

    @Null(groups = FieldCheck.class)
    private PictureLinkDTO avatar;

    @Valid
    private UserLinkDTO mainCaptain;

    @Valid
    private UserLinkDTO viceCaptain;


    public interface IdCheck extends TeamDTO.VersionCheck {

    }

    public interface CreateCheck extends TeamDTO.FieldCheck {

    }

    public interface UpdateCheck extends TeamDTO.VersionCheck, TeamDTO.FieldCheck {

    }

    private interface FieldCheck extends LinkedDTO.LinkCheck {

    }

    private interface VersionCheck {

    }
}