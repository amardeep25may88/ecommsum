package com.ecommsum.config;

import com.ecommsum.filters.JwtAuthFilter;
import com.ecommsum.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain basicAuth(HttpSecurity httpSecurity){
       return httpSecurity
               .authorizeHttpRequests(auth-> auth

                       // ===============================
                       // DEFAULT EXCLUDED API/URLS
                       // ===============================

                       .requestMatchers("/api/v1/user/home").permitAll()
                       .requestMatchers("/api/v1/user/jwtauthentication").permitAll()
                       .requestMatchers("/api/v1/user/create").hasRole("ADMIN")

                        // ===============================
                        // AUTHENTICATED API/URLS
                        // ===============================

                       .anyRequest().authenticated()
               )

               .csrf(AbstractHttpConfigurer::disable)

               // ===============================
               // AUTHENTICATION TYPES
               // ===============================

//               .httpBasic(Customizer.withDefaults())
//               .formLogin(Customizer.withDefaults())
               .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

               .build();

    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserEntityService();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserEntityService userEntityService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userEntityService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
