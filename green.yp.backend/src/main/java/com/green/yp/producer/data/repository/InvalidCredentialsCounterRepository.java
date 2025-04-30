package com.green.yp.producer.data.repository;

import com.green.yp.producer.data.model.InvalidCredentialsCounter;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvalidCredentialsCounterRepository
    extends JpaRepository<InvalidCredentialsCounter, UUID> {

  @Query(
      """
                        SELECT COUNT(*)
                        FROM InvalidCredentialsCounter ctr
                        WHERE
                            ctr.userId=:userId AND ctr.badCreds=:password AND ctr.ipAddress=:ipAddress
                    """)
  Integer countBadAttempts(
      @Param("userId") String userId,
      @Param("password") String password,
      @Param("ipAddress") String ipAddress);
}
