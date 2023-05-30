package com.example.customerservice.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                        .dispatcherTypeMatchers(DispatcherType.FORWARD,
                                DispatcherType.ERROR).permitAll()
                        .requestMatchers("/", "/v3/api-docs","/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers( "/customers", "/customers/getAll", "/customers/getById/**").permitAll()
                        .requestMatchers("/customers/deleteById/**", "/customers/add").hasRole("ADMIN")
                        .requestMatchers("/customers/**").authenticated()
                )
                .formLogin(Customizer.withDefaults());
        http.cors().disable().csrf().disable();
        return http.build();
    }

  /*  @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password("{bcrypt}$2a$10$/vCuzJ7a0e12oGuCJEHBbuno4RyroPhYclYohvwevG0cCapR0Qhmq")
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password("{bcrypt}$2a$10$oT6usX0w.haEtVfbJiwVrO0xNQVVjX9LBMX6H8Aa/prRqXBW/vGTy").roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }


   */


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


