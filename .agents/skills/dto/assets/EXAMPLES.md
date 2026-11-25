# Request DTO — Examples

## Full record example
```java
public record CreateCustomerRequest(
    @NotBlank @Size(max = 150) String firstName,  // see FIELD-VALIDATIONS.md for full @Pattern set
    @NotBlank @Size(max = 150) String lastName,
    @NotBlank @Email @Size(max = 254) String email,
    @NotNull UUID accountId,
    @NotNull @DecimalMin("0.00") @DecimalMax("1000000.00") @Digits(integer = 7, fraction = 2) BigDecimal creditLimit,
    @Valid @NotNull Address address
) {
    public record Address(
        @NotBlank @Size(max = 150) String line1,
        @NotBlank String city,  // see FIELD-VALIDATIONS.md for @Pattern set
        @NotBlank @Size(min = 2, max = 2) @Pattern(regexp = "^[A-Z]+$") String state,
        @NotBlank @Size(min = 5, max = 10) @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$") String postalCode
    ) {}
}
```
