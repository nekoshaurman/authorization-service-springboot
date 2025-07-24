package neko.authorization.service.controller;

import neko.authorization.service.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/debug-role")
    public ResponseEntity<String> giveAdminToUser(@RequestParam String login) {
        try {
            roleService.updateUserRoles(login, "ADMIN");
            return ResponseEntity.ok("Give ADMIN role to " + login);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid operation: " + e.getMessage());
        }
    }

    @PostMapping("/add-role")
    public ResponseEntity<String> addRoleToUser(@RequestParam String token, @RequestParam String login, @RequestParam String role) {
        try {
            if (roleService.isUserAdmin(token)) {
                roleService.updateUserRoles(login, role);
                return ResponseEntity.ok("Give " + role + " role to " + login);
            } else {
                return ResponseEntity.ok("User with " + token + " token not ADMIN");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid operation" + e.getMessage());
        }
    }
}
