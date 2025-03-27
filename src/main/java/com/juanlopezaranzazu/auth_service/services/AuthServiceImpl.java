package com.juanlopezaranzazu.auth_service.services;

import com.juanlopezaranzazu.auth_service.dtos.*;
import com.juanlopezaranzazu.auth_service.entities.PasswordResetToken;
import com.juanlopezaranzazu.auth_service.entities.Role;
import com.juanlopezaranzazu.auth_service.entities.User;
import com.juanlopezaranzazu.auth_service.exceptions.*;
import com.juanlopezaranzazu.auth_service.repositories.IPasswordResetTokenRepository;
import com.juanlopezaranzazu.auth_service.repositories.IRoleRepository;
import com.juanlopezaranzazu.auth_service.repositories.IUserRepository;
import com.juanlopezaranzazu.auth_service.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements IAuthService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IPasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final IEmailService emailService;
    private final String DEFAULT_ROLE_NAME = "ROLE_USER"; // Nombre del rol por defecto

    public AuthServiceImpl(IUserRepository userRepository, IRoleRepository roleRepository,
            IPasswordResetTokenRepository passwordResetTokenRepository, JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService, IEmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    @Override
    public UserResponse register(RegisterRequest registerRequest) {
        // Validar si ya existe el correo
        Optional<User> userWithSameEmail = userRepository.findByEmail(registerRequest.getEmail());
        if (userWithSameEmail.isPresent()) {
            throw new BadRequestException("El correo " + registerRequest.getEmail() + " ya está en uso");
        }

        // Validar que el rol exista y esté activo
        Role role = roleRepository.findByName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new NotFoundException("El rol " + DEFAULT_ROLE_NAME + " no existe"));

        // Crear un usuario
        User newUser = new User();
        newUser.setFirstName(registerRequest.getFirstName());
        newUser.setLastName(registerRequest.getLastName());
        newUser.setEmail(registerRequest.getEmail());
        // Encriptar contraseña
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setPhone(registerRequest.getPhone());
        newUser.setAddress(registerRequest.getAddress());
        newUser.setRole(role);

        // Guardar y devolver DTO
        User savedUser = userRepository.save(newUser);
        return convertToResponse(savedUser);
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        // Verificar si el correo y la contraseña son correctas
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        // Generar y devolver el token JWT
        String token = jwtUtil.generateToken(userDetails.getUsername());

        return new AuthResponse(token);
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        // Obtener usuario autenticado desde el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        // Buscar usuario en la base de datos
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Contraseña actual incorrecta");
        }

        // Verificar que la nueva contraseña sea igual a la confirmación
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmationPassword())) {
            throw new BadRequestException("Las contraseñas no coinciden");
        }

        // Encriptar nueva contraseña y actualizar en la base de datos
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserResponse me() {
        // Obtener usuario autenticado desde el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        // Buscar usuario en la base de datos
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        return convertToResponse(user);
    }

    @Override
    public void sendPasswordResetToken(String email) {
        // Buscar usuario en la base de datos
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // Expira en 1 hora

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiryDate);
        passwordResetTokenRepository.save(resetToken);

        // Enviar correo con el token
        String resetLink = "http://localhost:9090/api/v1/auth/reset-password?token=" + token;
        emailService.sendEmail(user.getEmail(), "Recuperación de Contraseña",
                "Haz clic en el siguiente enlace para restablecer tu contraseña: " + resetLink);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // Buscar token en la base de datos
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Token inválido"));

        // Verificar que el token no haya expirado
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Token expirado");
        }

        // Encriptar nueva contraseña y actualizar en la base de datos
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Eliminar token de la base de datos
        passwordResetTokenRepository.delete(resetToken);
    }

    // Devolver un DTO
    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole().getName());
    }
}
