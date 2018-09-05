package ru.vldf.sportsportal.mapper.generic;

import ru.vldf.sportsportal.domain.generic.AbstractDictionaryEntity;
import ru.vldf.sportsportal.dto.generic.AbstractDictionaryDTO;

public interface AbstractDictionaryMapper<E extends AbstractDictionaryEntity, D extends AbstractDictionaryDTO> extends AbstractWordbookMapper<E, D> {

    default E merge(E acceptor, E donor) {
        AbstractWordbookMapper.super.merge(acceptor, donor);
        acceptor.setDescription(donor.getDescription());
        return acceptor;
    }
}