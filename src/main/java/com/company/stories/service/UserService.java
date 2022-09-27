package com.company.stories.service;

import com.company.stories.exception.RoleNotFoundException;
import com.company.stories.exception.UserAlreadyExistsException;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.entity.Role;
import com.company.stories.model.entity.User;
import com.company.stories.repository.RoleRepository;
import com.company.stories.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private static final String DEFAULT_ROLE = "user";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User saveNewUser(UserDTO userDTO){
        Optional<User> dbUser = userRepository.findByEmail(userDTO.getEmail());

        if(dbUser.isPresent())
            throw new UserAlreadyExistsException(String.format("User with email %s already exist", userDTO.getEmail()));

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Optional<Role> role = roleRepository.findByName(DEFAULT_ROLE);

        Set<Role> userRoles = Set.of(role.orElseThrow(() -> new RoleNotFoundException(String.format("Role %s not found", DEFAULT_ROLE))));

        User user = User.builder()
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .roles(userRoles)
                .build();

        try {
            return userRepository.saveAndFlush(user);
        } catch (Exception ex){
            log.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            log.error("User with email {} not found", email);
            throw new UsernameNotFoundException("User not found");
        }

        User user = userOptional.get();

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
