package neko.authorization.service.service;

import neko.authorization.service.model.Role;
import neko.authorization.service.model.User;
import neko.authorization.service.repository.UserRepository;
import neko.authorization.service.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

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
