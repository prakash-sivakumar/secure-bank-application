package com.example.banking.bank_app;

import com.example.banking.bank_app.model.Auth_role;
import com.example.banking.bank_app.model.Auth_user;
import com.example.banking.bank_app.respository.AuthUserRepository;
import com.example.banking.bank_app.respository.AuthUserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private AuthUserRoleRepository authUserRoleRepository;

//    @Autowired
//    private LoginAttemptService loginAttemptService;

    @Autowired
    private HttpServletRequest request;

    public MyUserDetailsService() {
        super();
    }

    // API

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
//        final String ip = getClientIP();
//        if (loginAttemptService.isBlocked(ip)) {
//            throw new RuntimeException("blocked");
//        }

        try {
            final Auth_user auth_user = authUserRepository.findUserByEmail(email);
            if (auth_user == null) {
                throw new UsernameNotFoundException("No user found with username: " + email);
            }

            return new org.springframework.security.core.userdetails.User(auth_user.getEmail(), auth_user.getPassword(), true, true, true, true, getGrantedAuthorities(auth_user.getRoles()));
        } catch (final Exception e) {
            throw new UsernameNotFoundException("No user found with username: " + email);
        }
    }

    // UTIL

//    private final Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
//        return getGrantedAuthorities(getPrivileges(roles));
//    }

//    private final List<String> getPrivileges(final Collection<Role> roles) {
//        final List<String> privileges = new ArrayList<String>();
//        final List<Privilege> collection = new ArrayList<Privilege>();
//        for (final Role role : roles) {
//            collection.addAll(role.getPrivileges());
//        }
//        for (final Privilege item : collection) {
//            privileges.add(item.getName());
//        }
//
//        return privileges;
//    }
//
    private final List<GrantedAuthority> getGrantedAuthorities(final Set<Auth_role> roles) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final Auth_role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return authorities;
    }
//
//    private final String getClientIP() {
//        final String xfHeader = request.getHeader("X-Forwarded-For");
//        if (xfHeader == null) {
//            return request.getRemoteAddr();
//        }
//        return xfHeader.split(",")[0];
//    }

}

