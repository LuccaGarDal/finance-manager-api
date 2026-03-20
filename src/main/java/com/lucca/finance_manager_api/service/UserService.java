package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.dto.RegisterRequestDTO;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.mapper.UserMapper;
import com.lucca.finance_manager_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    public User register (RegisterRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        return userRepository.save(user);
    }
}
