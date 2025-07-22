package neko.authorization.service.controller;

import neko.authorization.service.security.JwtUtils;
import neko.authorization.service.service.AuthService;
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
            String token = authService.generateJwtToken(login);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}