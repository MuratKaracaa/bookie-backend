package com.karacam.bookie.services;

import com.karacam.bookie.core.localization.LocalizationManager;
import com.karacam.bookie.entities.AppUser;
import com.karacam.bookie.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    private final LocalizationManager localizationManager;

    @Autowired
    public AppUserDetailsService(AppUserRepository appUserRepository, LocalizationManager localizationManager) {
        this.appUserRepository = appUserRepository;
        this.localizationManager = localizationManager;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(email);
        if (optionalAppUser.isEmpty()) {
            String userNotFoundExceptionMessage = this.localizationManager.getLocalization("user_not_found_error");
            throw new UsernameNotFoundException(String.format(userNotFoundExceptionMessage, email));
        }
        AppUser appUser = optionalAppUser.get();

        return User.builder()
                .username(appUser.getEmail())
                .password(appUser.getPassword())
                .accountLocked(appUser.isLocked())
                .credentialsExpired(appUser.isPasswordExpired())
                .authorities(appUser.getAuthority().toString())
                .build();
    }
}
