package ru.vldf.sportsportal.mapper.sectional.lease;

import org.mapstruct.Mapper;
import ru.vldf.sportsportal.domain.sectional.lease.SportEntity;
import ru.vldf.sportsportal.dto.sectional.lease.SportDTO;
import ru.vldf.sportsportal.mapper.generic.AbstractDictionaryMapper;

@Mapper(componentModel = "spring")
public interface SportMapper extends AbstractDictionaryMapper<SportEntity, SportDTO> {

}
