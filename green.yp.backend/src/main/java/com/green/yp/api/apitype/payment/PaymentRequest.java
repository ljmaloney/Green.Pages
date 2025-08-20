package com.green.yp.api.apitype.payment;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record PaymentRequest(
    @NotNull @NonNull String paymentToken,
    String paymentNonce,
    @NotNull String verificationToken,
    String referenceId,
    String ipAddress,
    String note,
    BigDecimal paymentAmount,
    BigDecimal appFeeAmount,
    BigDecimal totalAmount,
    String statementDescription,
    @Pattern(
            regexp = "^[\\p{Alpha} ]*$",
            message = "First Name should contain only alphabets and space")
        @Pattern(
            regexp = "^[A-Za-z\\s\\-.']+$",
            message = "First Name should contain only alphabets, spaces, or hyphens")
        @Pattern(regexp = "^[^\\s].*$", message = "First Name should not start with space")
        @Pattern(regexp = "^.*[^\\s]$", message = "First Name should not end with space")
        @Pattern(
            regexp = "^((?!  ).)*$",
            message = "First Name should not contain consecutive spaces")
        @Pattern(
            regexp = "^[^a-z].*$",
            message = "First Name should not start with a lower case character")
        String firstName,
    @Pattern(
            regexp = "[\\p{Alpha} ]*$",
            message = "First Name should contain only alphabets and space")
        @Pattern(
            regexp = "^[A-Za-z\\s\\-.']+$",
            message = "First Name should contain only alphabets, spaces, or hyphens")
        @Pattern(regexp = "^[^\\s].*$", message = "First Name should not start with space")
        @Pattern(regexp = "^.*[^\\s]$", message = "First Name should not end with space")
        @Pattern(
            regexp = "^((?!  ).)*$",
            message = "First Name should not contain consecutive spaces")
        @Pattern(
            regexp = "^[^a-z].*$",
            message = "First Name should not start with a lower case character")
        String lastName,
    @NotBlank(message = "Enter the street address for accurate location sorting") String address,
    @NotBlank(message = "Enter the city for the classified listing")
        @Pattern(
            regexp = "^[A-Za-z\\s\\-.']+$",
            message = "City should contain only alphabets, spaces, or hyphens")
        @Pattern(regexp = "^[^\\s].*$", message = "City should not start with space")
        @Pattern(regexp = "^.*[^\\s]$", message = "City should not end with space")
        @Pattern(regexp = "^((?!  ).)*$", message = "City should not contain consecutive spaces")
        @Pattern(
            regexp = "^[^a-z].*$",
            message = "City should not start with a lower case character")
        String city,
    @Size(min = 2, max = 2, message = "Enter the two digit state abbreviation")
        @Pattern(regexp = "^[A-Z]+$", message = "State should contain only alphabets")
        String state,
    @NotBlank
        @Size(min = 5, max = 10, message = "Please enter the postal code")
        @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Enter a valid US Postal Code")
        String postalCode,
    @NotBlank(message = "Enter your phone number")
        @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
        String phoneNumber,
    @Email String emailAddress) {}
