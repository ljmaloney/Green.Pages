package com.green.yp.api.apitype.classified;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.NonNull;

public record ClassifiedRequest(
    @NotNull @NonNull UUID adType,
    @NotNull @NonNull UUID categoryId,
    @NotNull @NonNull BigDecimal price,
    @Pattern(
            regexp = "^[\\p{Alpha} ]*$",
            message = "Price per units should contain only alphabets and space")
        @Pattern(
            regexp = "^[A-Za-z\\s\\-.']+$",
            message = "Price per units should contain only alphabets, spaces, or hyphens")
        String pricePerUnitType,
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
        String lastName,
    @NotBlank(message = "Enter the street address for accurate location sorting") String address,
    @NotBlank(message = "Enter the city for the classified listing") String city,
    @Size(min = 2, max = 2, message = "Enter the two digit state abbreviation") String state,
    @NotBlank
        @Size(min = 5, max = 10, message = "Please enter the postal code")
        @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Enter a valid US Postal Code")
        String postalCode,
    @NotBlank(message = "Enter your phone number")
        @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
        String phoneNumber,
    @Email String emailAddress,
    @NotBlank(message = "Please enter ad title") String title,
    @NotBlank(message = "Please enter a classified ad description") String description) {}
