package com.devsenior.soledad.reservas_backend.service;

import com.devsenior.soledad.reservas_backend.dto.AuthResponseDTO;
import com.devsenior.soledad.reservas_backend.dto.LoginRequestDTO;
import com.devsenior.soledad.reservas_backend.dto.RegisterRequestDTO;
import com.devsenior.soledad.reservas_backend.entity.AppUser;
import com.devsenior.soledad.reservas_backend.entity.Company;
import com.devsenior.soledad.reservas_backend.entity.Role;
import com.devsenior.soledad.reservas_backend.exception.BadRequestException;
import com.devsenior.soledad.reservas_backend.exception.NotFoundException;
import com.devsenior.soledad.reservas_backend.repository.CompanyRepository;
import com.devsenior.soledad.reservas_backend.repository.UserRepository;
import com.devsenior.soledad.reservas_backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(CompanyRepository companyRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new company and its admin user using the slug provided in the request.
     *
     * @param request registration payload
     * @return authentication response with JWT token and tenant data
     */
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        String companyName = request.companyName().trim();
        String slug = request.slug().trim().toLowerCase();

        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username is already in use.");
        }

        if (companyRepository.findByName(companyName).isPresent()) {
            throw new BadRequestException("Company name is already in use.");
        }

        if (companyRepository.existsBySlug(slug)) {
            throw new BadRequestException("Slug is already in use. Please choose a different public link.");
        }

        Company company = Company.builder()
                .name(companyName)
                .slug(slug)
                .build();
        Company savedCompany = companyRepository.save(company);

        AppUser admin = AppUser.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ADMIN)
                .company(savedCompany)
                .build();

        AppUser savedUser = userRepository.save(admin);

        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getId(), savedCompany.getId(), savedUser.getRole().name());
        return new AuthResponseDTO(
                token,
                savedCompany.getId(),
                savedUser.getUsername(),
                savedUser.getRole().name(),
                savedCompany.getSlug()
        );
    }

    /**
     * Authenticates a user and returns an access token response.
     *
     * @param request login payload
     * @return authenticated response with JWT token and tenant data
     * @throws BadCredentialsException when username or password is invalid
     */
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        AppUser user = userRepository.findByUsernameWithCompany(request.username())
                .orElseThrow(() -> new NotFoundException("User not found."));

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getId(),
                user.getCompany().getId(),
                user.getRole().name()
        );
        return new AuthResponseDTO(
                token,
                user.getCompany().getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getCompany().getSlug()
        );
    }
}
