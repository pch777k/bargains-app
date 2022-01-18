package com.pch777.bargains.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.security.web.firewall.RequestRejectedHandler;

import com.pch777.bargains.security.UserPrincipalDetailsService;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private UserPrincipalDetailsService userPrincipalDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setUserDetailsService(this.userPrincipalDetailsService);

		return daoAuthenticationProvider;
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/h2-console/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/bargains/**", "/api/comments/**", "api/users/*/comments", "/api/activities/*","api/users/*/activities").permitAll()
				.antMatchers(HttpMethod.GET, "api/users", "api/users/*", "api/users/photo/*").authenticated()
				.antMatchers(HttpMethod.POST, "/api/bargains", "/api/bargains/*/photo", "/api/bargains/*/comments","/api/votes/*").authenticated()
				.antMatchers(HttpMethod.PUT, "/api/bargains/*", "api/users/*/password", "api/users/*", "/api/comments/*").authenticated()
				.antMatchers(HttpMethod.PATCH, "/api/bargains/*", "/api/comments/*").authenticated()
				.antMatchers(HttpMethod.DELETE, "/api/bargains/*","api/users/*", "/api/comments/*").authenticated()
				.antMatchers("/bargains/add", "/bargains/*/edit", "/bargains/*/delete", "/bargains/*/open", "/bargains/*/close").authenticated()
				.antMatchers("/comments/add","/bargains/*/comments/*/edit","/bargains/*/comments/*/cite", "/bargains/*/comments/*/delete").authenticated()
				.antMatchers("/votes/*", "/vote-bargain/*").authenticated()
				.antMatchers("/users","/users/*/profile", "/users/*/password").authenticated()
				.antMatchers("/users/*/delete").hasAuthority("ADMIN")				
				.antMatchers("/**").permitAll()	
				.and()
				.formLogin()
				.loginPage("/login")
				.usernameParameter("email").passwordParameter("password").defaultSuccessUrl("/").permitAll()
				.and()
				.logout().logoutSuccessUrl("/").permitAll();

		http.sessionManagement()
        		.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
		http.httpBasic();
		http.csrf().disable();
	}
	
	@Bean
	RequestRejectedHandler requestRejectedHandler() {
	   return new HttpStatusRequestRejectedHandler();
	}

}
