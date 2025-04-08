package com.copay.app.dto.auth;

public class CountryDialCodeDTO {

    private String countryCode;
    private String countryName;
    private String dialCode;
    private String flagEmoji;

    // Empty constructur.
    public CountryDialCodeDTO() {}

    // Constructor
    public CountryDialCodeDTO(String countryCode, String countryName, String dialCode, String flagEmoji) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.dialCode = dialCode;
        this.flagEmoji = flagEmoji;
    }

    // Getters y Setters
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getFlagEmoji() {
        return flagEmoji;
    }

    public void setFlagEmoji(String flagEmoji) {
        this.flagEmoji = flagEmoji;
    }
}
