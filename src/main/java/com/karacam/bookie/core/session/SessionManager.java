package com.karacam.bookie.core.session;

import com.karacam.bookie.core.BaseConstants;
import com.karacam.bookie.core.config.ConfigManager;
import com.karacam.bookie.core.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@Component
public class SessionManager {

    private final AuthenticationManager authenticationManager;
    private final ConfigManager configManager;


    @Autowired
    public SessionManager(AuthenticationManager authenticationManager, ConfigManager configManager) {
        this.authenticationManager = authenticationManager;
        this.configManager = configManager;
    }

    public HttpSession createAuthenticatedSession(String email, String password) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                email, password);
        Authentication authentication = this.authenticationManager.authenticate(token);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);

        HttpSession httpSession = request.getSession(true);

        httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        return httpSession;
    }

    public HttpSession createAnonymousSession() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String anonymousPrincipal = BaseConstants.ANONYMOUS_USER + "-" + UUID.randomUUID().toString();
        AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
                BaseConstants.ANONYMOUS_SESSION_KEY,
                anonymousPrincipal,
                Collections.singletonList(new SimpleGrantedAuthority(Role.ANONYMOUS.toString()))
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(anonymousToken);

        HttpSession httpSession = request.getSession(true);

        httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        httpSession.setMaxInactiveInterval(this.configManager.getIntConfig("cache_expiry_duration", 300000) / 1000);

        return httpSession;
    }

    public HttpSession getCurrentSession() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getSession(false);
    }

    public String getCurrentUsername() {
        HttpSession session = getCurrentSession();
        if (session != null) {
            SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            Authentication authentication = securityContext.getAuthentication();
            return authentication != null ? authentication.getName() : null;
        }
        return null;
    }

    public void addCustomDataToSession(String key, Object value) {
        HttpSession session = getCurrentSession();
        if (session != null) {
            session.setAttribute(key, value);
        }
    }

    public Object getCustomDataFromSession(String key) {
        HttpSession session = getCurrentSession();
        if (session != null) {
            return session.getAttribute(key);
        }
        return null;
    }

    public void removeDataFromSession(String attributeName) {
        HttpSession httpSession = getCurrentSession();
        httpSession.removeAttribute(attributeName);
    }

    public void killSession() {
        HttpSession httpSession = getCurrentSession();
        httpSession.invalidate();
    }
}
