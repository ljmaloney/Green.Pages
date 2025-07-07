package com.green.yp.api.apitype.classified;

import jakarta.validation.constraints.*;
import lombok.NonNull;

import java.math.BigDecimal;

public record ClassifiedUpdateRequest(@NotNull @NonNull BigDecimal price,
                                      @Pattern(regexp = "^[\\p{Alpha} ]*$", message = "Price per units should contain only alphabets and space")
                                      @Pattern(regexp = "^[A-Za-z\\s\\-.']+$", message = "Price per units should contain only alphabets, spaces, or hyphens")
                                      String pricePerUnitType,
                                      String address,
                                      @NotBlank(message = "Enter the city for the classified listing")
                                      String city,
                                      @Size(min =2, max = 2, message = "Enter the two digit state abbreviation")
                                      String state,
                                      @NotBlank
                                      @Size(min=5, max=10, message = "Please enter the postal code")
                                      @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Enter a valid US Postal Code")
                                      String postalCode,
                                      @NotBlank(message = "Enter your phone number")
                                      @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
                                      String phoneNumber,
                                      @Email
                                      String emailAddress,
                                      @Pattern(regexp = "^[\\p{Alpha} ]*$", message = "Title should contain only alphabets and space")
                                      @Pattern(regexp = "^[A-Za-z\\s\\-.']+$", message = "Title should contain only alphabets, spaces, or hyphens")
                                      @NotBlank(message = "Please enter ad title")
                                      @Pattern(regexp = "^[\\p{Alpha} ]*$", message = "Title should contain only alphabets and space")
                                      @Pattern(regexp = "^[A-Za-z\\s\\-.']+$", message = "Title should contain only alphabets, spaces, or hyphens")
                                      String title,
                                      @NotBlank(message = "Please enter a classified ad description")
                                      String description) {}
