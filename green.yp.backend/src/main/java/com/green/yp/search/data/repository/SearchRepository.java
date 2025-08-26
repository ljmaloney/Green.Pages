package com.green.yp.search.data.repository;

import com.green.yp.api.apitype.enumeration.SearchRecordType;
import com.green.yp.search.data.entity.SearchDistanceProjection;
import com.green.yp.search.data.entity.SearchMaster;
import com.green.yp.search.data.entity.SearchRecord;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends JpaRepository<SearchMaster, UUID> {
  @Query(
      value =
          """
              SELECT
                bin_to_uuid(sm.id) as id,
                bin_to_uuid(sm.producer_id) as producerId,
                bin_to_uuid(sm.location_id) as locationId,
                ST_Distance_Sphere(sm.location_geo_point, ST_GeomFromText( ?1, 4326))/1609.34 AS distance
              FROM search_master sm
              WHERE sm.active = 'Y'
                AND (?3 IS NULL OR sm.category_ref = ?3)
                AND (?4 IS NULL OR MATCH(keywords, title, description) AGAINST (?4))
                AND (?2 IS NULL OR ST_Distance_Sphere(sm.location_geo_point, ST_GeomFromText(?1, 4326)) <= ?2 )
                AND (sm.last_active_date is NULL OR sm.last_active_date >= current_date)
              ORDER BY distance ASC
              """,
      countQuery =
          """
              SELECT COUNT(*)
              FROM search_master sm
             WHERE sm.active = 'Y'
                AND (?3 IS NULL OR sm.category_ref = ?3)
                AND (?4 IS NULL OR MATCH(keywords, title, description) AGAINST (?4))
                AND (?2 IS NULL OR ST_Distance_Sphere(sm.location_geo_point, ST_GeomFromText(?1, 4326)) <= ?2 )
                AND (sm.last_active_date is NULL OR sm.last_active_date >= current_date)
             """,
      nativeQuery = true)
  Page<SearchDistanceProjection> executeSearch(
      @Param("wktPoint") String wktPoint,
      @Param("distanceFilter") BigDecimal distanceFilter,
      @Param("categoryId") UUID categoryId,
      @Param("keywords") String keywords,
      Pageable pageable);

  @Query(
      """
              SELECT new com.green.yp.search.data.entity.SearchRecord(
                  search,
                  CAST(ROUND((3959.0 * acos(cos(radians(:latitude)) * cos(radians(search.latitude)) *
                              cos(radians(search.longitude) - radians(:longitude)) +
                             sin(radians(:latitude)) * sin(radians(search.latitude)))), 2)
                  AS java.math.BigDecimal)
          )
          FROM SearchMaster search
          WHERE search.id IN :searchMasterIds
          ORDER BY
              (3959.0 * acos(cos(radians(:latitude)) * cos(radians(search.latitude)) *
              cos(radians(search.longitude) - radians(:longitude)) +
              sin(radians(:latitude)) * sin(radians(search.latitude)))) ASC,
              search.businessName ASC, search.recordType ASC
          """)
  List<SearchRecord> loadSearchResults(
      @Param("searchMasterIds") List<UUID> searchIds,
      @Param("latitude") Double latitude,
      @Param("longitude") Double longitude);

  @Modifying
  void deleteSearchMasterByExternId(@NotNull UUID externRefId);

  @Modifying
  @Query("""
          UPDATE SearchMaster sm SET sm.lastActiveDate =:lastActiveDate, sm.lastUpdateDate = :updateDate
          WHERE sm.producerId =:producerId
    """)
  int disableSearch(@NotNull @Param("producerId}")UUID producerId,
                    @Param("lastActiveDate") LocalDate lastActiveDate,
                    @Param("updateDate")OffsetDateTime updateDate);

  @Query(""" 
     SELECT sm FROM SearchMaster sm WHERE sm.externId=:externId AND sm.customerRef=:customerRef
   """)
  Optional<SearchMaster> findSearchMaster(@NotNull @NotNull @Param("externId") UUID externId,
                                          @NotNull @NotNull @Param("customerRef") String customerRef);

  @Query("""
            SELECT sm
            FROM SearchMaster sm
            WHERE sm.externId=:externId AND sm.producerId=:producerId
                  AND sm.locationId=:locationId AND sm.categoryRef=:categoryRef
        """)
  Optional<SearchMaster> findSearchMaster(@NotNull @NotNull @Param("externId") UUID externId,
                        @NotNull @NotNull @Param("producerId") UUID producerId,
                        @NotNull @NotNull @Param("locationId") UUID locationId,
                        @NotNull @NotNull @Param("categoryRef") UUID categoryRef);

    @Query("""
            SELECT sm
            FROM SearchMaster sm
            WHERE sm.producerId=:producerId
              AND sm.locationId=:locationId
              AND sm.recordType IN (:recordTypes)
        """)
    List<SearchMaster> findSearchMaster(@NotNull @NotNull @Param("externId") UUID externId,
                                            @NotNull @NotNull @Param("producerId") UUID producerId,
                                            @NotNull @NotNull @Param("locationId") UUID locationId,
            @NotNull @NotNull @Param("recordTypes") SearchRecordType...recordTypes);

    @Modifying
    @Query("DELETE FROM SearchMaster sm WHERE sm.producerId IN :producerIds")
    void deleteSearchMasterByProducerIds(@Param("producerIds") List<UUID> producerIds);

    void deleteSearchMasterByExternIdAndRecordType(UUID externId, SearchRecordType recordType);

  @Modifying
  @Query("UPDATE SearchMaster sm SET sm.businessIconUrl=:urlPath WHERE sm.producerId=:producerId")
  void updateBusinessIconUrl(
      @Param("producerId") UUID producerId, @Param("urlPath") String urlPath);

    @Modifying
    @Query("UPDATE SearchMaster sm SET sm.businessIconUrl=NULL WHERE sm.producerId=:producerId")
    void deleteBusinessIconUrl(@Param("producerIds") UUID producerId);


}
