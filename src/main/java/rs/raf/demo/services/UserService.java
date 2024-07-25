package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.User;
import rs.raf.demo.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService, IService<User, Long> {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User myUser = this.userRepository.findByUsername(username);
        if(myUser == null) {
            throw new UsernameNotFoundException("User name "+username+" not found");
        }

        List<GrantedAuthority> authorities = Arrays.stream(myUser.getPermissions().split(","))
                .map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission.trim()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(myUser.getUsername(), myUser.getPassword(), authorities);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
