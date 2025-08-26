package com.green.yp.api.contract;

import com.green.yp.api.apitype.enumeration.SearchRecordType;
import com.green.yp.api.apitype.search.SearchLocationUpdateRequest;
import com.green.yp.api.apitype.search.SearchMasterRequest;
import com.green.yp.search.service.SearchV2Service;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SearchContract {
    private final SearchV2Service searchV2Service;

    public SearchContract(SearchV2Service searchV2Service) {
        this.searchV2Service = searchV2Service;
    }

    public void deleteSearchMaster(@NotNull UUID externRefId) {
        searchV2Service.deleteSearchMaster(externRefId);
    }

    public void disableSearchMaster(@NotNull UUID producerId, @NotNull LocalDate lastActiveDate) {
        searchV2Service.disableProducerSearch(producerId, lastActiveDate);
    }

    public void createSearchRecords(List<SearchMasterRequest> searchList) {
        searchV2Service.createSearchMaster(searchList);
    }

  public void upsertSearchMaster(List<SearchMasterRequest> searchRequests, UUID custonerRefId) {
        searchV2Service.upsertSearchMaster(searchRequests,custonerRefId);
    }

    public void deleteSearchMaster(List<UUID> producerIds, String ipAddress) {
        searchV2Service.deleteProducerSearchMaster(producerIds);
    }

    public void deleteSearchMaster(UUID externId, SearchRecordType recordType) {
        searchV2Service.deleteProducerSearchMaster(externId, recordType);
    }

    public void upsertIconLink(UUID producerId, String urlPath) {
        searchV2Service.upsertProducerIconLink(producerId, urlPath);
    }

    public void updateLocation(SearchLocationUpdateRequest searchLocationUpdate) {
        searchV2Service.updateLocation(searchLocationUpdate);
    }
}
