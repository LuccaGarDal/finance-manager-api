package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.config.TokenConfig;
import com.lucca.finance_manager_api.dto.auth.LoginRequestDTO;
import com.lucca.finance_manager_api.dto.auth.RegisterRequestDTO;
import com.lucca.finance_manager_api.dto.auth.RegisterResponseDTO;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.mapper.UserMapper;
import com.lucca.finance_manager_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenConfig tokenConfig;

    public RegisterResponseDTO register (RegisterRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        User save = userRepository.save(user);
        return userMapper.toRegisterResponse(save);
    }

    public String login (LoginRequestDTO dto) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication auth = authenticationManager.authenticate(userAndPass);

        User user = (User) auth.getPrincipal();
        return tokenConfig.generateToken(user);
    }
}
