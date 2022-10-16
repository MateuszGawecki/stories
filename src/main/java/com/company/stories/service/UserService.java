package com.company.stories.service;

import com.company.stories.exception.CannotDeleteFriendshipException;
import com.company.stories.exception.UserAlreadyExistsException;
import com.company.stories.exception.CannotCreateFriendshipException;
import com.company.stories.exception.UserNotFoundException;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.entity.Role;
import com.company.stories.model.entity.User;
import com.company.stories.model.mapper.UserMapper;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private static final String DEFAULT_ROLE = "user";

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public void assingRoleToUser(Long userId, String roleName) {
        User user = findUser(userId);

        Role role = roleService.findRoleByName(roleName);

        log.info("Assigning role {} to user {}", roleName, user.getName());

        Set<Role> userRoles = user.getRoles();

        userRoles.add(role);

        userRepository.save(user);
    }

    public void revokeRoleFromUser(Long userId, String roleName) {
        User user = findUser(userId);

        Role role = roleService.findRoleByName(roleName);

        log.info("Revoking role {} form user {}", roleName, user.getEmail());

        user.getRoles().remove(role);

        userRepository.save(user);
    }

    //TODO do przerobienia -> system powinien prosić o potwierdzenie
    public void addFriendForUser(Long userId, Long friendId) {
        if(userId.equals(friendId))
            throw new CannotCreateFriendshipException("Cannot create friendship between one pearson");

        User user = findUser(userId);
        User friend = findUser(friendId);

        if(user.getFriends().contains(friend))
            throw new CannotCreateFriendshipException(String.format("Friendship with user %s for user %s already created", user.getEmail(), friend.getEmail()));

        user.getFriends().add(friend);

        userRepository.save(user);
    }

    //TODO powinno usunąć dwustronnie
    public void removeFriendForUser(Long userId, Long friendId) {
        User user = findUser(userId);
        User friend = findUser(friendId);

        if(!user.getFriends().contains(friend))
            throw new CannotDeleteFriendshipException(String.format("User %s is not in friendship with %s", user.getEmail(), friend.getEmail()));

        user.getFriends().remove(friend);

        userRepository.save(user);
    }

    public List<UserDTO> getUserFriends(Long userId){
        User dbUser = findUser(userId);

        Set<User> userFriends = dbUser.getFriends();

        List<UserDTO> friendsDTOs= userFriends.stream().map(UserMapper::toUserDTO).collect(Collectors.toList());

        return friendsDTOs;
    }

    private User findUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty())
            throw new UserNotFoundException(String.format("User with id %d not found", userId));

        return  user.get();
    }

    public List<UserDTO> getAllUsers(){
        List<User> users = userRepository.findAll();

        List<UserDTO> userDTOS = users.stream().map(UserMapper::toUserDTO).collect(Collectors.toList());

        return userDTOS;
    }

    public User saveNewUser(UserDTO userDTO){
        Optional<User> dbUser = userRepository.findByEmail(userDTO.getEmail());

        if(dbUser.isPresent())
            throw new UserAlreadyExistsException(String.format("User with email %s already exist", userDTO.getEmail()));

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Set<Role> userRoles = Set.of(roleService.findRoleByName(DEFAULT_ROLE));

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
