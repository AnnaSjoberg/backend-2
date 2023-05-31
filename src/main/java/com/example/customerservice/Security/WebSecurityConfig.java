package com.example.customerservice.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/", "/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/customers", "/customers/getAll", "/customers/getById/**").permitAll()
                        .requestMatchers("/customers/deleteById/**", "/customers/add").hasRole("ADMIN")
                        .requestMatchers("/customers/**").authenticated()
                )
                .formLogin((formLogin) ->
                        formLogin.defaultSuccessUrl("/customers/welcome")) // Set the desired success URL
                .cors().disable()
                .csrf().disable();
        return http.build();
    }


    @Autowired
    private Environment environment;
    @Value("${customer-service.sec-password}")
    private String customerPW;

    @Value("${item-service.sec-password}")
    private String itemPW;

    @Value("${orders-service.sec-password}")
    private String orderPW;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        PasswordEncoder passwordEncoder = passwordEncoder();

        UserDetails customerUser = User.withUsername("customerUser")
                .password(passwordEncoder.encode(customerPW))
                .roles("USER", "ADMIN")
                .build();
        UserDetails itemUser = User.withUsername("itemUser")
                .password(passwordEncoder.encode(itemPW))
                .roles("USER")
                .build();
        UserDetails ordersUser = User.withUsername("ordersUser")
                .password(passwordEncoder.encode(orderPW))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(customerUser,itemUser,ordersUser);
    }



}


