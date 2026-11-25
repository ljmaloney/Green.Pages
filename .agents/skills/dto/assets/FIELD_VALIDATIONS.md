# Field Validation Annotations Reference

Complete annotation sets for domain-specific fields. Apply these exactly when the field name matches.

## `firstName`
```java
@NotBlank(message = "First Name is required")
@Size(max = 150)
@Pattern(regexp = "[\\p{Alpha} ]*$", message = "First Name should contain only alphabets and space")
@Pattern(regexp = "^[A-Za-z\\s\\-.']+$", message = "First Name should contain only alphabets, spaces, or hyphens")
@Pattern(regexp = "^[^\\s].*$", message = "First Name should not start with space")
@Pattern(regexp = "^.*[^\\s]$", message = "First Name should not end with space")
@Pattern(regexp = "^((?!  ).)*$", message = "First Name should not contain consecutive spaces")
@Pattern(regexp = "^[^a-z].*$", message = "First Name should not start with a lower case character")
String firstName;
```

## `lastName`
```java
@NotBlank(message = "Last Name is required")
@Size(max = 150)
@Pattern(regexp = "[\\p{Alpha} ]*$", message = "Last Name should contain only alphabets and space")
@Pattern(regexp = "^[A-Za-z\\s\\-.']+$", message = "Last Name should contain only alphabets, spaces, or hyphens")
@Pattern(regexp = "^[^\\s].*$", message = "Last Name should not start with space")
@Pattern(regexp = "^.*[^\\s]$", message = "Last Name should not end with space")
@Pattern(regexp = "^((?!  ).)*$", message = "Last Name should not contain consecutive spaces")
@Pattern(regexp = "^[^a-z].*$", message = "Last Name should not start with a lower case character")
String lastName;
```

## `city`
```java
@NotBlank(message = "City is required")
@Pattern(regexp = "^[A-Za-z\\s\\-.']+$", message = "City should contain only alphabets, spaces, or hyphens")
@Pattern(regexp = "^[^\\s].*$", message = "City should not start with space")
@Pattern(regexp = "^.*[^\\s]$", message = "City should not end with space")
@Pattern(regexp = "^((?!  ).)*$", message = "City should not contain consecutive spaces")
@Pattern(regexp = "^[^a-z].*$", message = "City should not start with a lower case character")
String city;
```

## `state`
```java
@NotBlank
@Size(min = 2, max = 2, message = "Enter the two digit state abbreviation")
@Pattern(regexp = "^[A-Z]+$", message = "State should contain only alphabets")
String state;
```

## `postalCode` / `zipCode` / `zip`
```java
@NotBlank
@Size(min = 5, max = 10, message = "Please enter the postal code")
@Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Enter a valid US Postal Code")
String postalCode;
```

## `addressLine1` / `addressLine2` / `street`
```java
@NotBlank
@Size(max = 150)
@Pattern(regexp = "^[0-9A-Za-z\\s\\-.,']+$", message = "Address should contain only letters, numbers, spaces, or allowed punctuation")
String addressLine1;
```

## `phoneNumber` / `telephoneNumber`
```java
@NotBlank
@Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 numeric digits")
String ctn;
```

## Currency fields (`amount`, `price`, `total`, `balance`, `creditLimit`)
```java
@NotNull
@DecimalMin(value = "0.00", message = "Amount must be zero or greater")
@DecimalMax(value = "9999999.99", message = "Amount exceeds maximum allowed value")
@Digits(integer = 7, fraction = 2)
BigDecimal amount;
```

## `name`-like fields (`name`, `companyName`)
- Required: `@NotBlank`, `@Size(max = 150)`
- Optional: `@Size(max = 150)`

## Pattern prompting for unspecified string fields
For any `String` without a domain-specific rule above, prompt:
1. Alpha-numeric only? → `@Pattern(regexp = "^[A-Za-z0-9]+$")`
2. Additional chars allowed? → build custom character class
3. No pattern needed? → omit `@Pattern`, use only `@NotBlank`/`@Size`
