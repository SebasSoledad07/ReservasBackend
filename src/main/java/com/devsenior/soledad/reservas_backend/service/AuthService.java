package com.devsenior.soledad.reservas_backend.service;

import com.devsenior.soledad.reservas_backend.dto.AuthResponseDTO;
import com.devsenior.soledad.reservas_backend.dto.RegisterRequestDTO;
import com.devsenior.soledad.reservas_backend.entity.AppUser;
import com.devsenior.soledad.reservas_backend.entity.Company;
import com.devsenior.soledad.reservas_backend.entity.Role;
import com.devsenior.soledad.reservas_backend.repository.CompanyRepository;
import com.devsenior.soledad.reservas_backend.repository.UserRepository;
import com.devsenior.soledad.reservas_backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(CompanyRepository companyRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    private String generateSlug(String companyName) {
        String nowhitespace = companyName.trim().replaceAll("\\s+", "-").toLowerCase(Locale.ROOT);
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^a-z0-9\\-]").matcher(normalized).replaceAll("");
        // ensure unique by appending numeric suffix if necessary
        String base = slug;
        int i = 1;
        while (companyRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + i++;
        }
        return slug;
    }

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        Company company = Company.builder().name(request.companyName()).slug(generateSlug(request.companyName())).build();
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
        return new AuthResponseDTO(token, savedCompany.getId(), savedUser.getUsername(), savedUser.getRole().name());
    }
}
