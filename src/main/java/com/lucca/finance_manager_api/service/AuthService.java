package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.config.TokenConfig;
import com.lucca.finance_manager_api.dto.auth.LoginRequestDTO;
import com.lucca.finance_manager_api.dto.auth.RegisterRequestDTO;
import com.lucca.finance_manager_api.dto.auth.RegisterResponseDTO;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.mapper.UserMapper;
import com.lucca.finance_manager_api.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Log4j2
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

    @Autowired
    RefreshTokenService refreshTokenService;

    public RegisterResponseDTO register (RegisterRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        User save = userRepository.save(user);
        log.info("Usuário {} registrado com sucesso", save.getEmail());
        return userMapper.toRegisterResponse(save);
    }

    public Map<String, String> login (LoginRequestDTO dto) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication auth = authenticationManager.authenticate(userAndPass);

        User user = (User) auth.getPrincipal();
        log.info("Usuário {} logado com sucesso", user.getEmail());

        String token = tokenConfig.generateToken(user);
        String refresh = refreshTokenService.createRefreshToken(user.getId()).getToken();

        return Map.of(
                "accessToken", token,
                "refreshToken", refresh
        );
    }
}
