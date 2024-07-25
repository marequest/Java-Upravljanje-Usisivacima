package rs.raf.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.User;
import rs.raf.demo.services.UserService;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private static final Logger log = LoggerFactory.getLogger(UserRestController.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    public UserRestController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(
            value = "/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<User>> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PERMISSION_can_read_users"))) {
                List<User> users = userService.findAll();
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PERMISSION_can_read_users"))) {
                Optional<User> optionalUser = userService.findById(id);
                if(optionalUser.isPresent()){
                    return ResponseEntity.ok(optionalUser.get());
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<User> createUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PERMISSION_can_create_users"))) {
                String sha256Password = this.passwordEncoder.encode(user.getPassword());
                user.setPassword(sha256Password);
                return ResponseEntity.ok(userService.save(user));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<User> updateUser(@RequestBody User updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PERMISSION_can_update_users"))) {
                Long userId = updatedUser.getUserId();
                Optional<User> existingUserOptional = userService.findById(userId);

                if (existingUserOptional.isPresent()) {
                    User existingUser = existingUserOptional.get();

                    if(!existingUser.getUsername().equalsIgnoreCase(updatedUser.getUsername())) {
                        existingUser.setUsername(updatedUser.getUsername());
                    }
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setPermissions(updatedUser.getPermissions());

                    User savedUser = userService.save(existingUser);

                    return ResponseEntity.ok(savedUser);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PERMISSION_can_delete_users"))) {
                userService.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}
