package neko.authorization.service.service;

import jakarta.transaction.Transactional;
import neko.authorization.service.model.RevokedToken;
import neko.authorization.service.model.Role;
import neko.authorization.service.model.User;
import neko.authorization.service.repository.RevokedTokenRepository;
import neko.authorization.service.repository.UserRepository;
import neko.authorization.service.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // reg new user
    @Transactional
    public User registerNewUser(String login, String email, String password) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new RuntimeException("Username already exists.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(password));

        Set<Role> roles = new HashSet<>();
        roles.add(Role.GUEST);
        newUser.setRoles(roles);

        return userRepository.save(newUser);
    }

    // auth login + password
    public boolean authenticateUser(String login, String password) {
        User user = userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));

        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    public String generateJwtToken(String login) {
        // Получаем пользователя
        User user = userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));

        // Генерируем JWT токен с ролями пользователя
        return jwtUtils.generateJwtToken(user.getLogin(), user.getRoles());
    }

    public String generateRefreshToken(String login) {
        // Получаем пользователя
        User user = userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));

        // Генерируем JWT токен с ролями пользователя
        return jwtUtils.generateRefreshToken(user.getLogin(), user.getRoles());
    }

    public Set<Role> getUserRoles(String login) {
        User user = userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles();
    }

    public void updateUserRoles(String login, String role) {
        Role validRole = validRole(role);

        if (validRole != null) {
            User user = userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));
            Set<Role> roles = getUserRoles(login);

            roles.add(validRole);
            user.setRoles(roles);

            userRepository.save(user);
        }
    }

    private Role validRole(String roleName) {
        try {
            return Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void revokeToken(String token) {
        RevokedToken revokedToken = new RevokedToken();
        revokedToken.setToken(token);
        revokedToken.setRevokedAt(new Date());
        revokedTokenRepository.save(revokedToken);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

    public boolean isUserAdmin(String token) {
        String login = jwtUtils.getUsernameFromJwtToken(token);
        User user = userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles().contains(Role.ADMIN);
    }

    public boolean isUserPremium(String token) {
        String login = jwtUtils.getUsernameFromJwtToken(token);
        User user = userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles().contains(Role.PREMIUM_USER);
    }
}