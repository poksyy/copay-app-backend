package com.copay.app.service.auth;

import com.copay.app.dto.auth.CountryDialCodeDTO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CountryCodeService {

    public List<CountryDialCodeDTO> getAllDialCodes() {
        return List.of(
                new CountryDialCodeDTO("ES", "Spain", "+34", "🇪🇸"),
                new CountryDialCodeDTO("FR", "France", "+33", "🇫🇷"),
                new CountryDialCodeDTO("US", "United States", "+1", "🇺🇸"),
                new CountryDialCodeDTO("MX", "Mexico", "+52", "🇲🇽"),
                new CountryDialCodeDTO("AR", "Argentina", "+54", "🇦🇷")
                // add more if it is necessary
        );
    }
}
