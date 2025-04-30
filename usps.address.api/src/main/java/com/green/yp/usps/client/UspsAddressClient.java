package com.green.yp.usps.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.green.yp.usps.exception.UspsApiErrorException;
import com.green.yp.usps.exception.UspsApiException;
import com.green.yp.usps.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;


@Slf4j
public class UspsAddressClient {

    private final String userId;
    private final String uspsUrl;
    public UspsAddressClient(String userId, String uspsUrl){
        this.userId = userId;
        this.uspsUrl = uspsUrl;
    }
    public AddressResponse validateAddress(String addressLine1,
                                           String addressLine2,
                                           String city,
                                           String state,
                                           String zip){

        String[] zipParts = this.splitZipCode(zip);

        AddressValidateRequest validateRequest = AddressValidateRequest.builder()
                .userId(userId)
                .revision(1)
                .address(Address.builder()
                        .addressId(0)
                        .addressLine1(addressLine2)
                        .addressLine2(addressLine1)
                        .city(city)
                        .state(state)
                        .zip(zipParts[0])
                        .zip4(zipParts[1])
                        .build())
                .build();

        XmlMapper xmlMapper = new XmlMapper();
        try {
            String addressStr = xmlMapper.writeValueAsString(validateRequest);

            final SimpleHttpRequest request = SimpleRequestBuilder
                    .get(uspsUrl)
                    .addParameter("API","Verify")
                    .addParameter("XML", addressStr)
                    .build();
            final CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                    .build();
            client.start();

            final Future<SimpleHttpResponse> future = client.execute(request, null);
            final SimpleHttpResponse response = future.get();
            client.close();

            String responseText = response.getBodyText();
            handleError(responseText);
            AddressValidateResponse validationResponse = xmlMapper.readValue(responseText, AddressValidateResponse.class);
            return validationResponse.getAddressResponse();

        } catch (IOException | ExecutionException | InterruptedException e) {
            log.error("Unexpected exception executing USPS api request", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * <?xml version="1.0" encoding="UTF-8"?>
     * <Error><Number>80040B19</Number><Description>XML Syntax Error:
     * Please check the XML request to see if it can be parsed.(B)
     * </Description><Source>USPSCOM::DoAuth</Source></Error>
     * @param responseText
     */
    private void handleError(String responseText) {
        if ( responseText.contains("<Error")){
            try {
                UspsApiError error = new XmlMapper().readValue(responseText, UspsApiError.class);
                throw new UspsApiErrorException(error.getErrorNumber(), error.getDescription(), error.getSource());
            } catch (JsonProcessingException e) {
                log.error("Unexpected exception deserializing Error response", e);
                throw new UspsApiException("Unexpected exception deserializing Error response", e);
            }
        }
    }

    private String[] splitZipCode(String zipCode){
        Pattern pattern = Pattern.compile("^\\d{5}-\\d{4}$");
        String[] zipParts = new String[2];
        if ( pattern.matcher(zipCode).matches()){
            List<MatchResult> results = pattern.matcher(zipCode).results().toList();
            zipParts[0] = results.get(0).toString();
            zipParts[1] = results.get(1).toString();
        }else{
            zipParts[0] = zipCode;
        }
        return zipParts;
    }
}
