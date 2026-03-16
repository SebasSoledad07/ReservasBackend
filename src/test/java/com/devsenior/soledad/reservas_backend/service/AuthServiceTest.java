package com.devsenior.soledad.reservas_backend.service;

import com.devsenior.soledad.reservas_backend.dto.AuthResponseDTO;
import com.devsenior.soledad.reservas_backend.dto.LoginRequestDTO;
import com.devsenior.soledad.reservas_backend.dto.RegisterRequestDTO;
import com.devsenior.soledad.reservas_backend.entity.AppUser;
import com.devsenior.soledad.reservas_backend.entity.Company;
import com.devsenior.soledad.reservas_backend.entity.Role;
import com.devsenior.soledad.reservas_backend.exception.BadRequestException;
import com.devsenior.soledad.reservas_backend.repository.CompanyRepository;
import com.devsenior.soledad.reservas_backend.repository.UserRepository;
import com.devsenior.soledad.reservas_backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldPersistRequestedSlugAndReturnToken() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "Acme Studio",
                "Mi-Link-Publico",
                "admin-acme",
                "secret123",
                "admin@acme.com"
        );

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(companyRepository.findByName("Acme Studio")).thenReturn(java.util.Optional.empty());
        when(companyRepository.existsBySlug("mi-link-publico")).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(jwtUtil.generateToken(anyString(), any(), any(), anyString())).thenReturn("jwt-token");
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company company = invocation.getArgument(0);
            company.setId(1L);
            return company;
        });
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        AuthResponseDTO response = authService.register(request);

        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(companyCaptor.capture());

        Company savedCompany = companyCaptor.getValue();
        assertEquals("Acme Studio", savedCompany.getName());
        assertEquals("mi-link-publico", savedCompany.getSlug());
        assertEquals("jwt-token", response.token());
        assertEquals(1L, response.companyId());
        assertEquals("admin-acme", response.username());
        assertEquals(Role.ADMIN.name(), response.role());
        assertEquals("mi-link-publico", response.slug());
    }

    @Test
    void registerShouldThrowWhenSlugAlreadyExists() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "Acme Studio",
                "mi-link-publico",
                "admin-acme",
                "secret123",
                "admin@acme.com"
        );

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(companyRepository.findByName("Acme Studio")).thenReturn(java.util.Optional.empty());
        when(companyRepository.existsBySlug("mi-link-publico")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(companyRepository, never()).save(any(Company.class));
        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void loginShouldReturnCompanySlugInAuthResponse() {
        LoginRequestDTO request = new LoginRequestDTO("admin-acme", "secret123");

        Company company = Company.builder()
                .id(1L)
                .name("Acme Studio")
                .slug("mi-link-publico")
                .build();

        AppUser user = AppUser.builder()
                .id(2L)
                .username("admin-acme")
                .password("encoded-password")
                .role(Role.ADMIN)
                .company(company)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsernameWithCompany("admin-acme")).thenReturn(java.util.Optional.of(user));
        when(jwtUtil.generateToken(anyString(), anyLong(), anyLong(), anyString())).thenReturn("jwt-token");

        AuthResponseDTO response = authService.login(request);

        assertEquals("jwt-token", response.token());
        assertEquals(1L, response.companyId());
        assertEquals("admin-acme", response.username());
        assertEquals(Role.ADMIN.name(), response.role());
        assertEquals("mi-link-publico", response.slug());
    }
}

