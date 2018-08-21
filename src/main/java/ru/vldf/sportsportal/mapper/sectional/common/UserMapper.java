package ru.vldf.sportsportal.mapper.sectional.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.vldf.sportsportal.domain.sectional.common.UserEntity;
import ru.vldf.sportsportal.dto.sectional.common.UserDTO;
import ru.vldf.sportsportal.dto.sectional.common.shortcut.UserShortDTO;
import ru.vldf.sportsportal.mapper.generic.AbstractVersionedMapper;
import ru.vldf.sportsportal.mapper.manual.url.common.PictureURLMapper;
import ru.vldf.sportsportal.mapper.manual.url.common.UserURLMapper;

import javax.persistence.OptimisticLockException;

@Mapper(
        componentModel = "spring",
        uses = {UserURLMapper.class, PictureURLMapper.class, PictureMapper.class, RoleMapper.class}
)
public interface UserMapper extends AbstractVersionedMapper<UserEntity, UserDTO> {

    @Mappings({
            @Mapping(target = "userURL", source = "id", qualifiedByName = {"toUserURL", "fromId"}),
            @Mapping(target = "avatarURL", source = "avatar", qualifiedByName = {"toPictureURL", "fromEntity"})
    })
    UserShortDTO toShortDTO(UserEntity entity);

    @Mapping(target = "url", source = "id", qualifiedByName = {"toUserURL", "fromId"})
    UserDTO toDTO(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    UserEntity toEntity(UserDTO dto);

    @Override
    default UserEntity merge(UserEntity acceptor, UserEntity donor) throws OptimisticLockException {
        AbstractVersionedMapper.super.merge(acceptor, donor);

        acceptor.setLogin(donor.getLogin());
        acceptor.setPassword(donor.getPassword());
        acceptor.setName(donor.getName());
        acceptor.setSurname(donor.getSurname());
        acceptor.setPatronymic(donor.getPatronymic());
        acceptor.setAddress(donor.getAddress());
        acceptor.setPhone(donor.getPhone());
        acceptor.setAvatar(donor.getAvatar());

        return acceptor;
    }
}
