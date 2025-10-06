package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class CheckoutRequest {
    private final String fullName;
    private final String email;
    private final String phone; // nullable
    private final String street1;
    private final String street2; // nullable
    private final String city;
    private final String region;
    private final String postalCode;
    private final String country;

    @JsonCreator
    public CheckoutRequest(
            @JsonProperty("fullName") @NotBlank(message = "Full name is required") @Size(max = 255, message = "Full name must not exceed 255 characters") String fullName,
            @JsonProperty("email") @NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 255, message = "Email must not exceed 255 characters") String email,
            @JsonProperty("phone") @Size(max = 50, message = "Phone must not exceed 50 characters") String phone,
            @JsonProperty("street1") @NotBlank(message = "Street address is required") @Size(max = 255, message = "Street address must not exceed 255 characters") String street1,
            @JsonProperty("street2") @Size(max = 255, message = "Street address line 2 must not exceed 255 characters") String street2,
            @JsonProperty("city") @NotBlank(message = "City is required") @Size(max = 255, message = "City must not exceed 255 characters") String city,
            @JsonProperty("region") @NotBlank(message = "Region/State is required") @Size(max = 255, message = "Region/State must not exceed 255 characters") String region,
            @JsonProperty("postalCode") @NotBlank(message = "Postal code is required") @Size(max = 32, message = "Postal code must not exceed 32 characters") String postalCode,
            @JsonProperty("country") @NotBlank(message = "Country is required") @Size(max = 255, message = "Country must not exceed 255 characters") String country
    ) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStreet1() { return street1; }
    public String getStreet2() { return street2; }
    public String getCity() { return city; }
    public String getRegion() { return region; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
}
