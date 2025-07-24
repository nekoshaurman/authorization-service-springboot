package neko.authorization.service.controller;

import neko.authorization.service.security.JwtUtils;
import neko.authorization.service.service.AuthService;
import neko.authorization.service.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public HttpStatus registerUser(@RequestParam String login,
                                   @RequestParam String email,
                                   @RequestParam String password) {
        try {
            authService.registerNewUser(login, email, password);
            return HttpStatus.CREATED;
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String login, @RequestParam String password) {
        if (authService.authenticateUser(login, password)) {
            String accessToken = authService.generateJwtToken(login);

            String refreshToken = authService.generateRefreshToken(login);

            return ResponseEntity.ok(accessToken + " " + refreshToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(@RequestParam String refreshToken) {
        if (authService.isTokenRevoked(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token. Token revoked");
        }

        if (jwtUtils.validateJwtToken(refreshToken)) {
            String username = jwtUtils.getUsernameFromJwtToken(refreshToken);

            String newAccessToken = authService.generateJwtToken(username);

            return ResponseEntity.ok(newAccessToken);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<String> revokeToken(@RequestParam String token) {
        if (jwtUtils.validateJwtToken(token)) {
            authService.revokeToken(token);
            return ResponseEntity.ok("Token has been revoked");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<String> getAdminResources(@RequestParam String token) {
        try {
            if (roleService.isUserAdmin(token)) {

                return ResponseEntity.ok("You have permission to access admin resources");
            } else {
                return ResponseEntity.ok("You haven't permission to access admin resources");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid operation" + e.getMessage());
        }
    }

    @PostMapping("/premium")
    public ResponseEntity<String> getPremiumResources(@RequestParam String token) {
        try {
            if (roleService.isUserPremium(token)) {

                return ResponseEntity.ok("You have permission to access premium resources");
            } else {
                return ResponseEntity.ok("You haven't permission to access premium resources");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid operation" + e.getMessage());
        }
    }
}