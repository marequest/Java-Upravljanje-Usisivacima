package rs.raf.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.model.User;
import rs.raf.demo.model.Usisivac;
import rs.raf.demo.repositories.ErrorMessageRepository;
import rs.raf.demo.repositories.UserRepository;
import rs.raf.demo.services.ErrorService;

import java.util.List;

@RestController
@RequestMapping("/api/errors")
@CrossOrigin(origins = "http://localhost:4200")
public class ErrorController {

    @Autowired
    private ErrorMessageRepository errorMessageRepository;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ErrorMessage>> getErrorMessages() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ErrorMessage> errorMessages = errorMessageRepository.findByUser(user);
        return ResponseEntity.ok(errorMessages);
//        return errorService.getErrorMessages();
    }
}
