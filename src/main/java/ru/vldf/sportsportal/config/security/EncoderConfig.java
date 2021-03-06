package ru.vldf.sportsportal.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Namednev Artem
 */
@Configuration
public class EncoderConfig {

    /**
     * BCryptPasswordEncoder configuration.
     *
     * @return password encoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
