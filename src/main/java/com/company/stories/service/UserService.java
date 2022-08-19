package com.company.stories.service;

import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.entity.User;
import com.company.stories.model.mapper.UserMapper;
import com.company.stories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        Optional<User> dbUser = userRepository.findById(user.getUser_id());

        if(dbUser.isEmpty())
            return null; //TODO exception

        return userRepository.save(user);
    }
}
