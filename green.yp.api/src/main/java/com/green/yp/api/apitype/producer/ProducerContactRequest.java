package com.green.yp.api.apitype.producer;

import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.apitype.producer.enumeration.ProducerDisplayContactType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
        @Size(max = 12, message = "The maximum length of a phone number is 12 characters")
        String phoneNumber,
        @Size(max = 12, message = "The maximum length of a cell phone number is 12 characters")
        String cellPhoneNumber,
        @Email(message = "The email address is not correctly formatted")
        @Size(max = 150, message = "The maximum length of an email address is 150 characters")
        String emailAddress) {
}
