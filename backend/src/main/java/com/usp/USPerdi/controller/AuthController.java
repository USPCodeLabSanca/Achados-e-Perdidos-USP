package com.usp.USPerdi.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import java.time.Instant;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final String googleClientId;
    public record TokenRequest(String token) {} 
    private final JwtEncoder jwtEncoder;

    public AuthController(JwtEncoder jwtEncoder, @Value("${auth.google.client-id}") String googleClientId) { //@Value para env
        this.jwtEncoder = jwtEncoder;
        this.googleClientId = googleClientId;
    }

    @PostMapping("/google")
    public ResponseEntity<String> googleAuth(@RequestBody TokenRequest request){
        try{
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()) 
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.token()); // Verificar se o token é google certified™ 

            if (idToken == null) { // Se não for válido 
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token.");
            }
            
            // Válido
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String nome = (String) payload.get("name");

            if (!email.endsWith("@usp.br")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied for users outside @usp.br");
            }

            // JWT
            Instant now = Instant.now();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("usperdi")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(36000)) // 10h
                    .subject(payload.getSubject()) // google id
                    .claim("email", email) 
                    .claim("name", nome) 
                    .build();

            String jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(); // Gera o JWT assinado

            return ResponseEntity.ok(jwt);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error authenticating: " + e.getMessage());
        }
    }
}