package io.codeblaze.reddit.services;

import io.codeblaze.reddit.dto.AuthenticationResponse;
import io.codeblaze.reddit.dto.LoginRequest;
import io.codeblaze.reddit.dto.SignupRequest;
import io.codeblaze.reddit.exceptions.SpringRedditException;
import io.codeblaze.reddit.model.NotificationEmail;
import io.codeblaze.reddit.model.User;
import io.codeblaze.reddit.model.VerificationToken;
import io.codeblaze.reddit.repository.UserRepository;
import io.codeblaze.reddit.repository.VerificationTokenRepository;
import io.codeblaze.reddit.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signup(SignupRequest signupRequest) {
        User user = User.builder()
            .username(signupRequest.getUsername())
            .email(signupRequest.getEmail())
            .password(passwordEncoder.encode(signupRequest.getPassword()))
            .created(Instant.now())
            .enabled(false)
            .build();

        userRepository.save(user);

        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail("Please Activate your account",
            user.getEmail(), "Thank you for signing up to Spring Reddit, " +
            "please click on the below url to activate your account : " +
            "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    private String generateVerificationToken(User user) {
        String verificationToken = UUID.randomUUID().toString();
        VerificationToken token = VerificationToken.builder()
            .token(verificationToken)
            .user(user)
            .build();

        verificationTokenRepository.save(token);

        return verificationToken;
    }

    @Transactional
    public void verifyAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new SpringRedditException("Invalid Token"));

        fetchUserAndEnable(verificationToken);

        verificationTokenRepository.delete(verificationToken);
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);

        return new AuthenticationResponse(token, loginRequest.getUsername());
    }

}
