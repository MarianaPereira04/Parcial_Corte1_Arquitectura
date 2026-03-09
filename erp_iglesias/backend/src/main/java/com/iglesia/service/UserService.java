package com.iglesia.service;

import com.iglesia.AppUser;
import com.iglesia.AppUserRepository;
import com.iglesia.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser createClient(String email, String password) {
        if (appUserRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado");
        }
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(UserRole.CLIENT);
        appUserRepository.save(user);
        return user;
    }

    public void createAdminIfNotExists() {
        if (!appUserRepository.existsByEmailIgnoreCase("admin@parroquia.com")) {
            AppUser admin = new AppUser();
            admin.setEmail("admin@parroquia.com");
            admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
            admin.setRole(UserRole.ADMIN);
            appUserRepository.save(admin);
        }
    }
}
