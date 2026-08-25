package com.campussync.service;

import com.campussync.dto.ParentRegistrationRequest;
import org.springframework.stereotype.Service;

@Service
public class ParentRegistrationService {

    public void registerParent(ParentRegistrationRequest request) {
        System.out.println("Registering parent: " + request.getEmail());
    }
}
