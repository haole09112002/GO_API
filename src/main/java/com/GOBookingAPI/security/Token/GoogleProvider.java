package com.GOBookingAPI.security.Token;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.security.Model.TokenSecurity;
import com.GOBookingAPI.security.Model.UserSecurity;

import lombok.extern.slf4j.Slf4j;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@Slf4j
@Component
public class GoogleProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    String CLIENT_ID_1 = "650109837523-52mh678rs1n5aa2nm9v1tl95fj7ig0sl.apps.googleusercontent.com";
    String CLIENT_ID_2 = "650109837523-8a0vnk2avi9e2oi0ktna07hv7gh5g8jl.apps.googleusercontent.com";
    String CLIENT_ID_3 = "650109837523-jlp9rv9eefsuaa3rp0no6tit7fhpfefo.apps.googleusercontent.com";
    String CLIENT_ID_4 = "650109837523-vcpbjogn6rgu2g4k1gojsfc5rtm5i7iq.apps.googleusercontent.com";

    private static final JacksonFactory jacksonFactory = new JacksonFactory();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            TokenSecurity token = (TokenSecurity) authentication;
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), jacksonFactory)
//						    .setAudience(Collections.singletonList(CLIENT_ID))
                    .setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3, CLIENT_ID_4))
                    .build();
            GoogleIdToken idToken = verifier.verify(token.getToken());
            if (idToken != null) {
                Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                Optional<User> userOptional = userRepository.findByEmail(email);
                if (userOptional.isEmpty()) {
                    User user = new User();
                    user.setEmail(email);
                    user.setIsNonBlock(true);
                    System.out.println("This is Provider and provider null");
                    return new UserSecurity(user, null);
                } else {
                    System.out.println("This is Provider");
                    User user = userOptional.get();
//                if (!user.getIsNonBlock()) {
//                   throw new AccessDeniedException("User account is locked");        //todo fix
//                }
                    return new UserSecurity(user, user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toString())).collect(Collectors.toList()));
                }
            } else {
                System.out.println("Invalid ID token.");
                return null;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.info("Fail in Provider ", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(TokenSecurity.class);
    }


}
