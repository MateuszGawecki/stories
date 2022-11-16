package com.company.stories.service;

import com.company.stories.exception.OperationNotPermittedException;
import com.company.stories.exception.author.AuthorNotFoundException;
import com.company.stories.exception.user.FriendshipNotFoundException;
import com.company.stories.exception.user.UserAlreadyExistsException;
import com.company.stories.exception.user.CannotCreateFriendshipException;
import com.company.stories.exception.user.UserNotFoundException;
import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.dto.UserRegistrationDTO;
import com.company.stories.model.entity.Book;
import com.company.stories.model.entity.Role;
import com.company.stories.model.entity.User;
import com.company.stories.model.mapper.BookMapper;
import com.company.stories.model.mapper.UserMapper;
import com.company.stories.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public UserDTO getUser(User user) {
        return UserMapper.toUserDTO(user);
    }

    public UserDTO getUser(Long userId) {
        return UserMapper.toUserDTO(userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Cannot find user with id %d", userId))));
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
    public void addFriendForUser(User user, Long friendId) {
        if(user.getUserId().equals(friendId))
            throw new CannotCreateFriendshipException("Cannot create friendship between one pearson");

        User friend = findUser(friendId);

        if(user.getFriends().contains(friend))
            throw new CannotCreateFriendshipException(String.format("Friendship with user %s for user %s already created", user.getEmail(), friend.getEmail()));

        user.getFriends().add(friend);

        userRepository.save(user);
    }

    //TODO powinno usunąć dwustronnie
    public void removeFriendForUser(User user, Long friendId) {
        User friend = findUser(friendId);

        if(!user.getFriends().contains(friend))
            throw new FriendshipNotFoundException(String.format("User %s is not in friendship with %s", user.getEmail(), friend.getEmail()));

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

    public Map<String, Object> getAllUsers(Pageable pageable){
        Page<User> page = userRepository.findAll(pageable);

        return getPageOfUsers(page);
    }

    public Map<String, Object> getByNameAndSurname(String searchValue, Pageable pageable) {
        String[] names = searchValue.split(" ");

        Page<User> page;

        if(names.length == 2) {
            page = userRepository.findByNameContainingAndSurnameContainingIgnoreCase(names[0], names[1], pageable);
        }else if(names.length == 1){
            page = userRepository.findByNameContainingIgnoreCase(names[0], pageable);
        }else {
            throw new OperationNotPermittedException("Cannot find user with more than 2 names");
        }

        return getPageOfUsers(page);
    }

    private Map<String, Object> getPageOfUsers(Page<User> page) {
        List<UserDTO> userDTOS = page.getContent().stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());

        Map<String, Object> userPage = new HashMap<>();
        userPage.put("users", userDTOS);
        userPage.put("currentPage", page.getNumber());
        userPage.put("totalItems", page.getTotalElements());
        userPage.put("totalPages", page.getTotalPages());

        return userPage;
    }

    public UserDTO saveNewUser(UserRegistrationDTO userRegistrationDTO){
        UserDTO userDTO = userRegistrationDTO.getUserDTO();

        Optional<User> dbUser = userRepository.findByEmail(userDTO.getEmail());

        if(dbUser.isPresent())
            throw new UserAlreadyExistsException(String.format("User with email %s already exist", userDTO.getEmail()));

        userRegistrationDTO.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));

        Set<Role> userRoles = Set.of(roleService.findRoleByName(DEFAULT_ROLE));

        User user = User.builder()
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .email(userDTO.getEmail())
                .password(userRegistrationDTO.getPassword())
                .roles(userRoles)
                .build();

        try {
            return UserMapper.toUserDTO(userRepository.saveAndFlush(user));
        } catch (Exception ex){
            log.error(ex.getMessage());
            return null;
        }
    }

    public void changeUserPassword(User user, String oldPassword, String newPassword){

        if(!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new OperationNotPermittedException("Provided password is not users password");

        String newEncodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(newEncodedPassword);

        try {
            userRepository.saveAndFlush(user);
        } catch (Exception ex){
            log.error(ex.getMessage());
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

    public User getUser(String username) {
        Optional<User> user = userRepository.findByEmail(username);

        return user.get(); //used when request passed security -> user exists
    }

    public Set<UserDTO> findByName(String name) {
        String[] names = name.split(" ");

        Set<User> byNames;

        if(names.length == 2) {
            byNames = userRepository.findByNameContainingAndSurnameContainingIgnoreCase(names[0], names[1]);
        }else if(names.length == 1){
            byNames = userRepository.findByNameContainingIgnoreCase(names[0]);
        }else {
            throw new AuthorNotFoundException("Cannot find author with more than 2 names");
        }

        Set<UserDTO> byNameDTOs = byNames.stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toSet());

        return byNameDTOs;
    }

    public UserDTO updateUser(User issuer) {
        return UserMapper.toUserDTO(userRepository.save(issuer));
    }
}
