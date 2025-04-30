package com.green.yp.reference.data.repository;

import com.green.yp.reference.data.enumeration.SubscriptionType;
import com.green.yp.reference.data.model.Subscription;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    @Query(
            """
                        select subs from Subscription subs
                        where subs.id=:subscriptionId and :currentDate between subs.startDate and subs.endDate
                    """)
    Optional<Subscription> findAllActive(
            @Param("subscriptionId") UUID subscriptionId, @Param("currentDate") Date date);

    @Query(
            """
                        SELECT subs
                        FROM Subscription subs
                        WHERE
                            :currentDate between subs.startDate and subs.endDate
                            and subs.subscriptionType in (:subscriptionTypes)
                        ORDER BY subs.sortOrder ASC
                    """)
    List<Subscription> findAllActive(
            @Param("currentDate") Date date,
            @Param("subscriptionTypes") SubscriptionType... subscriptionTypes);

    @Query(
            """
                        select subs
                        from Subscription subs
                        where
                            subs.lineOfBusinessId = :lineOfBusinessId
                            and :currentDate between subs.startDate and subs.endDate
                            and subs.subscriptionType in (:subscriptionTypes)
                    """)
    List<Subscription> findAllActive(
            @Param("lineOfBusiness") UUID lineOfBusinessId,
            @Param("currentDate") Date date,
            @Param("subscriptionTypes") SubscriptionType... subscriptionType);
}
