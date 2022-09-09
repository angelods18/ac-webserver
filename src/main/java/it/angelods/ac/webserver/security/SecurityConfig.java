package it.angelods.ac.webserver.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Value("${credentials.username}")
	private String username;
	
	@Value("${credentials.password}")
	private String password;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {		// TODO Auto-generated method stub
		http.csrf().disable();
		
		CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration
                .setAllowedOrigins(List.of("http://localhost:4200", "https://ac-gaeta-website.herokuapp.com"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "Diocesi"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        http
        	.cors().configurationSource(request -> corsConfiguration)
        	.and()
        	.authorizeRequests()
        		.antMatchers(HttpMethod.POST,"incontro/salva-incontro", "evento/salva-evento")
        		.authenticated()
            .and()
            .httpBasic();

        
        return http.build();
	}
	
	@Bean
	  public InMemoryUserDetailsManager userDetailsService() {
	    UserDetails user = User
	        .withUsername(username)
	        .password("{noop}"+password)
	        .roles("USER")
	        .build();
	    return new InMemoryUserDetailsManager(user);
	  }
}
