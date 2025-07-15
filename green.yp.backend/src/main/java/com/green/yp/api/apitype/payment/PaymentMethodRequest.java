package com.green.yp.api.apitype.payment;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record PaymentMethodRequest(@NotNull @NonNull String paymentToken,
                                   @NotNull @NonNull String verificationToken,
                                   @NotBlank String emailValidationToken,
                                   String referenceId,
                                   String firstName,
                                   @Pattern(regexp = "[\\p{Alpha} ]*$", message = "First Name should contain only alphabets and space")
                                   @Pattern(regexp = "^[A-Za-z\\s\\-.']+$", message = "First Name should contain only alphabets, spaces, or hyphens")
                                   @Pattern(regexp = "^[^\\s].*$", message = "First Name should not start with space")
                                   @Pattern(regexp = "^.*[^\\s]$", message = "First Name should not end with space")
                                   @Pattern(regexp = "^((?!  ).)*$", message = "First Name should not contain consecutive spaces")
                                   @Pattern(regexp = "^[^a-z].*$", message = "First Name should not start with a lower case character")
                                   @NotNull @NonNull String lastName,
                                   String companyName,
                                   @NotBlank(message = "Enter the street address for accurate location sorting")
                                   @NotNull @NonNull String payorAddress1,
                                   String payorAddress2,
                                   @NotBlank(message = "Enter the city for the for the subscription listing")
                                   @Pattern(regexp = "^[A-Za-z\\s\\-.']+$", message = "City should contain only alphabets, spaces, or hyphens")
                                   @Pattern(regexp = "^[^\\s].*$", message = "City should not start with space")
                                   @Pattern(regexp = "^.*[^\\s]$", message = "City should not end with space")
                                   @Pattern(regexp = "^((?!  ).)*$", message = "City should not contain consecutive spaces")
                                   @Pattern(regexp = "^[^a-z].*$", message = "City should not start with a lower case character")
                                   @NotNull @NonNull String payorCity,
                                   @Size(min =2, max = 2, message = "Enter the two digit state abbreviation")
                                   @Pattern(regexp = "^[A-Z]+$", message = "State should contain only alphabets")
                                   @NotNull @NonNull String payorState,
                                   @NotBlank
                                   @Size(min=5, max=10, message = "Please enter the postal code")
                                   @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Enter a valid US Postal Code")
                                   @NotNull @NonNull String payorPostalCode,
                                   @NotBlank(message = "Enter your phone number")
                                   @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
                                   String phoneNumber,
                                   @Email
                                   String emailAddress) {}
