package pl.skup.vinted.springPackage.services;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.skup.vinted.models.requestmodel.TokenRequest;

@Service
@AllArgsConstructor
public class LogInService {

    public static String token;

    public ResponseEntity<String> logInToVinted(TokenRequest tokenRequest) {
        token = tokenRequest.cookie;
        return ResponseEntity.ok("Poprawnie zapisano cookiesa");
    }
}
