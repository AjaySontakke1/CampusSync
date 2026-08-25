package com.campussync.service;

import com.campussync.dto.ParentRegistrationRequest;
import com.campussync.entity.Parent;
import com.campussync.entity.User;
import com.campussync.enums.Role;
import com.campussync.repository.ParentRepository;
import com.campussync.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParentRegistrationService {

    private final UserRepository userRepository;
    private final ParentRepository parentRepository;

    public ParentRegistrationService(UserRepository userRepository, ParentRepository parentRepository) {
        this.userRepository = userRepository;
        this.parentRepository = parentRepository;
    }

    @Transactional
    public void registerParent(ParentRegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.PARENT);
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        Parent parent = new Parent();
        parent.setFirstName(request.getFirstName());
        parent.setLastName(request.getLastName());
        parent.setPhone(request.getPhone());
        parent.setUser(savedUser);

        parentRepository.save(parent);
    }
}
