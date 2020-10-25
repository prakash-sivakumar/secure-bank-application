package com.example.banking.bank_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


	@Autowired
	private CustomLoginSuccessHandler successHandler;

	@Autowired
	private UserDetailsService userDetailsService;


	@Autowired
	private CustomWebAuthenticationDetailsSource authenticationDetailsSource;


	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.formLogin().authenticationDetailsSource(authenticationDetailsSource);
		http.authorizeRequests()
				// URLs matching for access rights
				.antMatchers("/").permitAll()
				.antMatchers("/login").permitAll()
				.antMatchers("/register").permitAll()
				.antMatchers("/home/**").hasAnyAuthority("ADMIN", "TIER1", "TIER2", "USER", "MERCHANT")
				.antMatchers("/admin/**").hasAnyAuthority("ADMIN")
				.antMatchers("/help/list/**").hasAnyAuthority("ADMIN")
				.antMatchers("/log/**").hasAnyAuthority("ADMIN")
				.antMatchers("/employee/create").hasAnyAuthority("ADMIN")
				.antMatchers("/employee/delete/**").hasAnyAuthority("ADMIN")
				.antMatchers("/employee/edit/**").hasAnyAuthority("ADMIN")
				.antMatchers("/employee/**").hasAnyAuthority("ADMIN","TIER2")
				.antMatchers("/addUser").hasAnyAuthority("TIER2")
				.antMatchers(HttpMethod.POST,"/account/new").hasAnyAuthority("USER","MERCHANT")
				.antMatchers(HttpMethod.GET,"/account/deposit").hasAnyAuthority("TIER1")
                .antMatchers(HttpMethod.GET, "/account/withdraw").hasAnyAuthority("TIER1")
                .antMatchers(HttpMethod.POST,"/account/deposit").hasAnyAuthority("TIER1","USER","MERCHANT")
				.antMatchers(HttpMethod.POST, "/account/withdraw").hasAnyAuthority("TIER1","USER","MERCHANT")
				.antMatchers("/request/**").hasAnyAuthority("TIER2", "TIER1")
				.antMatchers("/tier1/**").hasAnyAuthority("TIER1")
				.antMatchers("/account/list/**").hasAnyAuthority("TIER1", "TIER2")
				.antMatchers("/account-request/**").hasAnyAuthority("TIER1", "ADMIN", "TIER2")
				.antMatchers("/checks/**").hasAnyAuthority("TIER1")
				.antMatchers("/transfer/**").hasAnyAuthority("TIER1", "USER", "MERCHANT")
				.antMatchers("/tier2/**").hasAnyAuthority("TIER2")
				.antMatchers("/log/**").hasAnyAuthority("ADMIN")
				.antMatchers("/help/list/**").hasAnyAuthority("ADMIN")
				//.antMatchers("/merchant/**").hasAnyAuthority("MERCHANT")
				.antMatchers("/user").hasAnyAuthority("USER","MERCHANT")
				.antMatchers("/user/edit").hasAnyAuthority("USER", "MERCHANT")
				.antMatchers("/account/deposit1").hasAnyAuthority("USER", "MERCHANT")
				.antMatchers("/account/withdraw1").hasAnyAuthority("USER", "MERCHANT")
				.antMatchers("/account/**").hasAnyAuthority("TIER2")
				.antMatchers("/help/helpform").hasAnyAuthority("USER", "MERCHANT")
				.antMatchers("/otp/generateOtp/**").permitAll()
				.antMatchers("/user/list/**").denyAll()
				.anyRequest().authenticated()
				.and()
				.csrf()
				.and()
				.formLogin().loginPage("/login")
				.failureUrl("/login?error=true")
				//.defaultSuccessUrl("/merchant")

				.successHandler(successHandler)
				.usernameParameter("email")
				.passwordParameter("password")
				.and()
				// logout
				.logout()
				//.addLogoutHandler(new CustomLogoutHandler())
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/").and()
				.exceptionHandling()
				.accessDeniedPage("/denied");

		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				.maximumSessions(1);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Bean
	public DaoAuthenticationProvider authProvider() {
		CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(encoder());
		return authProvider;
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

}
