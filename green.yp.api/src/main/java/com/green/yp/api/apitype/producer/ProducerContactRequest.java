package com.green.yp.api.apitype.producer;

import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.apitype.producer.enumeration.ProducerDisplayContactType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.NonNull;

public record ProducerContactRequest(
    UUID contactId,
    UUID producerLocationId,
    @NotNull @NonNull ProducerContactType producerContactType,
    @NotNull @NonNull ProducerDisplayContactType displayContactType,
    String genericContactName,
    String firstName,
    String lastName,
    String title,
    @Size(max = 16, message = "The maximum length of a phone number is 12 characters")
    @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
        String phoneNumber,
    @Size(max = 16, message = "The maximum length of a cell phone number is 12 characters")
    @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
        String cellPhoneNumber,
    @Email(message = "The email address is not correctly formatted")
        @Size(max = 150, message = "The maximum length of an email address is 150 characters")
        String emailAddress,
        Boolean importFlag) {

    public boolean isNotImported(){
        return importFlag != null ? !importFlag : Boolean.TRUE;
    }
}
