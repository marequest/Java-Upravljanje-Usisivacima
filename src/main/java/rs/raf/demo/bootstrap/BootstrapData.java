package rs.raf.demo.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.raf.demo.model.*;
import rs.raf.demo.repositories.*;

import java.util.*;

@Component
public class BootstrapData implements CommandLineRunner {


    private final UserRepository userRepository;

    private final UsisivacRepository usisivacRepository;

    private final ErrorMessageRepository errorMessageRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BootstrapData(UsisivacRepository usisivacRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ErrorMessageRepository errorMessageRepository) {
        this.usisivacRepository = usisivacRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.errorMessageRepository = errorMessageRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading Data...");

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword(this.passwordEncoder.encode("user1"));
        user1.setFirstName("Vasilije");
        user1.setLastName("Nikolić");
        user1.setPermissions("can_read_users,can_create_users,can_update_users,can_delete_users,can_search_vacuum,can_start_vacuum,can_stop_vacuum,can_discharge_vacuum,can_add_vacuum,can_remove_vacuums,can_schedule_vacuum");
        this.userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword(this.passwordEncoder.encode("user2"));
        user2.setFirstName("Marko");
        user2.setLastName("Radovanović");
        user2.setPermissions("can_read_users,can_search_vacuum,can_start_vacuum,can_stop_vacuum,can_discharge_vacuum,can_schedule_vacuum");
        this.userRepository.save(user2);

        Usisivac usisivac1 = new Usisivac();
        usisivac1.setStatus(Usisivac.Status.OFF);
        usisivac1.setAddedBy(user1);
        usisivac1.setName("usisivac1");
        usisivac1.setActive(true);
        usisivac1.setCreatedDate(new Date());
        this.usisivacRepository.save(usisivac1);

        Usisivac usisivac2 = new Usisivac();
        usisivac2.setStatus(Usisivac.Status.OFF);
        usisivac2.setAddedBy(user1);
        usisivac2.setName("Nikola");
        usisivac2.setActive(true);
        usisivac2.setCreatedDate(new Date());
        this.usisivacRepository.save(usisivac2);

        Usisivac usisivac3 = new Usisivac();
        usisivac3.setStatus(Usisivac.Status.OFF);
        usisivac3.setAddedBy(user2);
        usisivac3.setName("usisivac3");
        usisivac3.setActive(true);
        usisivac3.setCreatedDate(new Date());
        this.usisivacRepository.save(usisivac3);

        Usisivac usisivac4 = new Usisivac();
        usisivac4.setStatus(Usisivac.Status.OFF);
        usisivac4.setAddedBy(user2);
        usisivac4.setName("usisivac4");
        usisivac4.setActive(true);
        usisivac4.setCreatedDate(new Date());
        this.usisivacRepository.save(usisivac4);

        System.out.println("Data loaded!");
    }
}
