package com.green.yp.api.contract;


import com.green.yp.api.apitype.producer.LocationRequest;
import com.green.yp.api.apitype.producer.ProducerLocationResponse;
import com.green.yp.producer.service.ProducerLocationService;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ProducerLocationContract {
    final
    ProducerLocationService locationService;

    public ProducerLocationContract(ProducerLocationService locationService) {
        this.locationService = locationService;
    }

    public ProducerLocationResponse findPrimaryLocation(UUID producerId) {
        return locationService.findPrimaryLocation(producerId);
    }

    public ProducerLocationResponse updatePrimaryLocation(@NotNull @NonNull UUID producerId,
                                                          @NotNull @NonNull LocationRequest primaryLocation,
                                                          @NotNull @NonNull String ipAddress) {

        if ( primaryLocation.locationId() == null ){
            return locationService.createLocation(primaryLocation, producerId, ipAddress);
        }
        locationService.findPrimaryLocation(producerId);
        return locationService.updateLocation(primaryLocation, null, ipAddress);
    }

    public void deleteLocation(@NotNull @NonNull List<UUID> producerIds) {
        locationService.deleteLocations(producerIds);
    }
}
