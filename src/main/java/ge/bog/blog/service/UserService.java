package ge.bog.blog.service;


import ge.bog.blog.entity.UserEntity;
import ge.bog.blog.exceptions.AlreadyExistsException;
import ge.bog.blog.model.AddUserReq;
import ge.bog.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    @Transactional
    public long addUser(AddUserReq user) {
        Optional<UserEntity> existingUserByUsername = userRepository.findByUserName(user.getUserName());
        if (existingUserByUsername.isPresent()) {
            throw new AlreadyExistsException("User with this username already exists");
        }
        Optional<UserEntity> existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent()) {
            throw new AlreadyExistsException("User with this email already exists");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(user.getUserName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userRepository.save(userEntity);
        return userEntity.getId();
    }
}