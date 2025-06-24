package com.green.yp.api.apitype.contact;

import com.green.yp.api.apitype.enumeration.ContactRequestType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

public record ContactRequest(ContactRequestType requestType,
                             ProducerLeadContactRequest leadContactRequest,
                             String companyName,
                             @NotNull @NonNull
                             @Email(message = "The email address is not correctly formatted")
                             @Size(max = 150, message = "The maximum length of an email address is 150 characters")
                             String emailAddress,
                             @Pattern(regexp = "^[\\p{Alpha} ]*$", message = "Name should contain only alphabets and space")
                             @Pattern(regexp = "^[A-Za-z\\s\\-.']+$", message = "Name should contain only alphabets, spaces, or hyphens")
                             @Pattern(regexp = "^[^\\s].*$", message = "Name should not start with space")
                             @Pattern(regexp = "^.*[^\\s]$", message = "Name should not end with space")
                             @Pattern(regexp = "^((?!  ).)*$", message = "Name should not contain consecutive spaces")
                             @Pattern(regexp = "^[^a-z].*$", message = "Name should not start with a lower case character")
                             String name,
                             @Size(max = 16, message = "The maximum length of a cell phone number is 12 characters")
                             @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
                             String phoneNumber,
                             @Pattern(regexp = "^[A-Za-z\\s\\-.']+$", message = "Subject should contain only alphabets, spaces, or hyphens")
                             String subject,
                             @Pattern(regexp = "^[A-Za-z\\s\\-.']+$", message = "Message should contain only alphabets, spaces, or hyphens")
                             String message) {}
