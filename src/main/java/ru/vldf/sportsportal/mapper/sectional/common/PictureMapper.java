package ru.vldf.sportsportal.mapper.sectional.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.vldf.sportsportal.domain.sectional.common.PictureEntity;
import ru.vldf.sportsportal.dto.sectional.common.specialized.PictureLinkDTO;
import ru.vldf.sportsportal.mapper.generic.LinkMapper;
import ru.vldf.sportsportal.mapper.manual.JavaTimeMapper;
import ru.vldf.sportsportal.mapper.manual.url.common.PictureURLMapper;

/**
 * @author Namednev Artem
 */
@Mapper(componentModel = "spring", uses = {JavaTimeMapper.class, PictureURLMapper.class})
public abstract class PictureMapper implements LinkMapper<PictureEntity, PictureLinkDTO> {

    @Mapping(target = "url", source = "id", qualifiedByName = {"toPictureURL", "fromId"})
    public abstract PictureLinkDTO toDTO(PictureEntity entity);
}
