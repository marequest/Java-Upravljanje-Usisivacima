package rs.raf.demo.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import rs.raf.demo.dto.ScheduleOperationRequest;
import rs.raf.demo.model.ScheduleOperation;
import rs.raf.demo.model.Usisivac;
import rs.raf.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.services.ScheduledOperationService;
import rs.raf.demo.services.UserService;
import rs.raf.demo.services.UsisivacService;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/usisivaci")
@CrossOrigin(origins = "http://localhost:4200")
public class UsisivacController {

    private static final Logger log = LoggerFactory.getLogger(UsisivacController.class);

    @Autowired
    private UsisivacService usisivacService;

    @Autowired
    private ScheduledOperationService scheduledOperationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Usisivac> getAllActiveUsisivaci(@AuthenticationPrincipal UserDetails userDetails) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PERMISSION_can_search_vacuum"))) {

                String username = userDetails.getUsername();
                User user = userService.getUserByUsername(username);

                return usisivacService.getAllActiveUsisivaciForUser(user);
            } else {
                log.error("User " + userDetails.getUsername() + " does not have permission to search for vacuum cleaners");
                return null;
            }
        } else {
            log.error("User is not authenticated");
            return null;
        }
    }

    @GetMapping("/search")
    public List<Usisivac> searchVacuums(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) List<String> status,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFrom,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateTo,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PERMISSION_can_search_vacuum"))) {

                String username = userDetails.getUsername();
                User user = userService.getUserByUsername(username);

                return usisivacService.searchVacuums(name, status, dateFrom, dateTo, user);
            } else {
                log.error("User " + userDetails.getUsername() + " does not have permission to search for vacuum cleaners");
                return null;
            }
        } else {
            log.error("User is not authenticated");
            return null;
        }
    }

    @PostMapping
    public Usisivac addUsisivac(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Usisivac usisivac) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PERMISSION_can_add_vacuum"))) {

                String username = userDetails.getUsername();
                User user = userService.getUserByUsername(username);
                usisivac.setAddedBy(user);

                return usisivacService.addUsisivac(usisivac);
            } else {
                log.error("User " + userDetails.getUsername() + " does not have permission to add vacuum cleaners");
                return null;
            }
        } else {
            log.error("User is not authenticated");
            return null;
        }
    }

    @DeleteMapping("/{id}")
    public void removeUsisivac(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PERMISSION_can_remove_vacuums"))) {

                usisivacService.removeUsisivac(id);
            } else {
                log.error("User does not have permission to remove vacuum cleaners");
            }
        } else {
            log.error("User is not authenticated");
        }
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Usisivac> startUsisivac(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("PERMISSION_can_start_vacuum"))) {
            log.error("User does not have permission to start vacuum cleaners");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Usisivac usisivac = usisivacService.startUsisivac(id);
            return ResponseEntity.ok(usisivac);
        } catch (RuntimeException e) {
//            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/stop")
    public ResponseEntity<Usisivac> stopUsisivac(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("PERMISSION_can_stop_vacuum"))) {
            log.error("User does not have permission to stop vacuum cleaners");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Usisivac usisivac = usisivacService.stopUsisivac(id);
            return ResponseEntity.ok(usisivac);
        } catch (RuntimeException e) {
//            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/discharge")
    public ResponseEntity<Usisivac> dischargeUsisivac(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("PERMISSION_can_discharge_vacuum"))) {
            log.error("User does not have permission to discharge vacuum cleaners");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Usisivac usisivac = usisivacService.dischargeUsisivac(id);
            return ResponseEntity.ok(usisivac);
        } catch (RuntimeException e) {
//            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/schedule")
    public ResponseEntity<Void> scheduleOperation(@PathVariable Long id,
                                                  @RequestBody ScheduleOperationRequest request) {
        System.out.println("Scheduling operation for vacuum cleaner with id " + id + ": " + request.getOperation() + " at " + request.getScheduledTime());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PERMISSION_can_schedule_vacuum"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ScheduleOperation.Operation op;
        try {
            op = ScheduleOperation.Operation.valueOf(request.getOperation().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        scheduledOperationService.scheduleOperation(id, op, request.getScheduledTime());
        return ResponseEntity.ok().build();
    }


}
