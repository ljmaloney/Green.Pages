package com.green.yp.producer.data.repository;

import com.green.yp.producer.data.model.ProducerUserCredentials;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerUserCredentialsRepository
    extends JpaRepository<ProducerUserCredentials, UUID> {
  @Query(
      """
                        SELECT authUser FROM ProducerUserCredentials authUser
                        WHERE authUser.userId=:userId and authUser.password=:password
                    """)
  Optional<ProducerUserCredentials> findCredentials(
      @NonNull @Param("userId") String userId, @NonNull @Param("password") String password);

  @Query(
      """
                        SELECT authUser FROM ProducerUserCredentials authUser WHERE authUser.userId=:userId
                    """)
  Optional<ProducerUserCredentials> findCredentials(
      @NonNull @NotNull @Param("userId") String userId);

  @Query(
      """
                        SELECT authUser
                        FROM ProducerUserCredentials authUser
                        WHERE authUser.producerContactId=:contactId
                    """)
  Optional<ProducerUserCredentials> findContactCredentials(
      @NonNull @NotNull @Param("contactId") UUID contactId);

  @Query(
      """
                        SELECT authUser
                        FROM ProducerUserCredentials authUser
                        WHERE authUser.producerId=:producerId
                            AND authUser.adminUser = true
                            AND authUser.enabled = true
                    """)
  Optional<ProducerUserCredentials> findMasterUserCredentials(
      @NonNull @NotNull @Param("producerId") UUID producerId);

  @Query(
      """
                SELECT credentials FROM ProducerUserCredentials credentials
                WHERE credentials.producerId IN (:producerIds)
            """)
  List<ProducerUserCredentials> findCredentials(
      @NonNull @NotNull @Param("producerIds") List<UUID> producerIdList);
}
