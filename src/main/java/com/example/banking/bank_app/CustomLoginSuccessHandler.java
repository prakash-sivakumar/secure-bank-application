package com.example.banking.bank_app;

import com.example.banking.bank_app.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private LogService logService;

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authntication) throws IOException {
        String targetUrl = determineTargetUrl(authntication);
        logService.saveLog(authntication.getName(), "User logged in!");
        if(response.isCommitted()) {
            return;
        }
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        String url = "/login?error=true";

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        if(roles.contains("ADMIN")) {
            url = "/admin";
        }
        else if(roles.contains("TIER1")) {
            url = "/tier1";
        }
        else if(roles.contains("TIER2")) {
            url = "/tier2";
        }
        else if(roles.contains("USER")) {
            url = "/user";
        }
        else if(roles.contains("MERCHANT")) {
            url = "/user";
        }
        return url;
    }

}
