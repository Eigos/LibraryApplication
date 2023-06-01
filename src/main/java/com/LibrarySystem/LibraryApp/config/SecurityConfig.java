package com.LibrarySystem.LibraryApp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());;
    }

    @Override
    public void configure(HttpSecurity web) throws Exception {
        web
        .httpBasic()
        .and()
        .requiresChannel().anyRequest().requiresSecure()
        .and()
        .csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET,"/admin").hasRole("ADMIN")

        //Account
        .antMatchers(HttpMethod.PUT,"/account/**").hasRole("ADMIN")
        .antMatchers(HttpMethod.GET,"/account/**").hasRole("ADMIN")
        .antMatchers(HttpMethod.POST,"/account").hasRole("ADMIN")
        
        //User
        .antMatchers(HttpMethod.GET,"/user/**").hasAnyRole("ADMIN")
        .antMatchers(HttpMethod.POST,"/user").hasAnyRole("ADMIN")

        //Book
        .antMatchers(HttpMethod.POST, "/book/**").hasAnyRole("ADMIN")
        .antMatchers(HttpMethod.PUT, "/book/**").hasAnyRole("ADMIN")
        .antMatchers(HttpMethod.GET, "/book").permitAll()

        //Author
        .antMatchers(HttpMethod.POST, "/author/**").hasAnyRole("ADMIN")
        .antMatchers(HttpMethod.GET, "/author/**").hasAnyRole("ADMIN", "USER")

        //Borrow Registration
        .antMatchers(HttpMethod.POST, "/borrow_registration/**").hasAnyRole("ADMIN")
        .antMatchers(HttpMethod.GET, "/borrow_registration/**").hasAnyRole("ADMIN")
        
        
        .antMatchers("/").permitAll()
        .and().formLogin().disable();

        web.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    PasswordEncoder getPasswordEncoder(){
        //TO-DO make it more secure
        return NoOpPasswordEncoder.getInstance();
    }
}
