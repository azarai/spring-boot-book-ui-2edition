package de.codeboje.springbootbook.commentstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import de.codeboje.springbootbook.commentstore.security.jwt.JwtAuthenticationProcessingFilter;
import de.codeboje.springbootbook.commentstore.security.jwt.JwtAuthenticationProvider;
import de.codeboje.springbootbook.commentstore.security.jwt.JwtRequestExtractor;
import de.codeboje.springbootbook.commentstore.security.jwt.RestAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationProvider jwtAuthenticationProvider;
	
	@Configuration
	@Order(1)
	public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
		@Autowired
		private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

		@Autowired
		private AuthenticationFailureHandler failureHandler;

		@Autowired
		private AuthenticationManager authenticationManager;

		protected JwtAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter() throws Exception {
			JwtAuthenticationProcessingFilter filter = new JwtAuthenticationProcessingFilter(failureHandler,
					new JwtRequestExtractor(), "/**");
			filter.setAuthenticationManager(this.authenticationManager);
			return filter;
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable()
				.cors()
				.and()
				.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
				.and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
					.authorizeRequests()
					.anyRequest().authenticated().and()
					.httpBasic()
					.and()
			 .addFilterAfter(buildJwtTokenAuthenticationProcessingFilter(),
			 BasicAuthenticationFilter.class)
			;
		}
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(jwtAuthenticationProvider);
		auth.inMemoryAuthentication().withUser("admin").password(passwordEncoder().encode("mypassword")).roles("ADMIN");
	}
	
	@Bean("myAuthenticationManager")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
