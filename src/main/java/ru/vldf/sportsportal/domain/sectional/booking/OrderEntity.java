package ru.vldf.sportsportal.domain.sectional.booking;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.vldf.sportsportal.domain.general.AbstractVersionedEntity;
import ru.vldf.sportsportal.domain.sectional.common.UserEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * @author Namednev Artem
 */
@Getter
@Setter
@Entity
@Table(name = "order", schema = "booking")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class OrderEntity extends AbstractVersionedEntity {

    @Basic
    @Column(name = "sum", nullable = false)
    private BigDecimal sum = BigDecimal.valueOf(0, 2);

    @Basic
    @Column(name = "datetime", nullable = false)
    private Timestamp datetime;

    @Basic
    @Column(name = "expiration")
    private Timestamp expiration;

    @Basic
    @Column(name = "paid", nullable = false)
    private Boolean isPaid = false;

    @Basic
    @Column(name = "owned", nullable = false)
    private Boolean isOwned = false;

    @Basic
    @Column(name = "freed", nullable = false)
    private Boolean isFreed = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private UserEntity customer;

    @OrderBy("pk.datetime")
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<ReservationEntity> reservations;
}
