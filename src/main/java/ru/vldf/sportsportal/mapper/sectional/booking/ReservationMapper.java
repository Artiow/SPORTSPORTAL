package ru.vldf.sportsportal.mapper.sectional.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.vldf.sportsportal.domain.sectional.booking.ReservationEntity;
import ru.vldf.sportsportal.dto.sectional.booking.specialized.ReservationResumeDTO;
import ru.vldf.sportsportal.mapper.general.BasicMapper;
import ru.vldf.sportsportal.mapper.manual.JavaTimeMapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Namednev Artem
 */
@SuppressWarnings("UnmappedTargetProperties")
@Mapper(uses = {PlaygroundMapper.class, JavaTimeMapper.class})
public abstract class ReservationMapper implements BasicMapper<ReservationEntity, ReservationResumeDTO.Item> {

    @Mapping(target = "datetime", source = "pk.datetime")
    public abstract ReservationResumeDTO.Item toDTO(ReservationEntity entity);

    @Mapping(target = "pk.datetime", source = "datetime")
    public abstract ReservationEntity toEntity(ReservationResumeDTO.Item dto);


    @Mapping(target = "playground", source = "pk.playground")
    public abstract ReservationResumeDTO toResume(ReservationEntity entity);


    public ReservationResumeDTO toResume(Collection<ReservationEntity> entityCollection) {
        if ((entityCollection == null) || entityCollection.isEmpty()) {
            return null;
        }

        // price sum
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (ReservationEntity entity : entityCollection) {
            totalPrice = totalPrice.add(entity.getPrice());
        }

        ReservationResumeDTO resume = toResume(entityCollection.iterator().next());
        resume.setReservations(toDTO(entityCollection));
        resume.setTotalPrice(totalPrice);
        return resume;
    }

    public List<ReservationResumeDTO> toResumeList(Collection<ReservationEntity> entityCollection) {
        if (entityCollection == null) {
            return null;
        }

        // grouping
        Map<Integer, List<ReservationEntity>> groups = entityCollection.stream().collect(Collectors.groupingBy(e -> e.getPlayground().getId()));
        return groups.entrySet().stream().map(e -> toResume(e.getValue())).collect(Collectors.toList());
    }
}
