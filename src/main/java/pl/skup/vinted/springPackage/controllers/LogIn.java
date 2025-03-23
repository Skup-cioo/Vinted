package pl.skup.vinted.springPackage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skup.vinted.models.requestmodel.TokenRequest;
import pl.skup.vinted.springPackage.services.LogInService;


@Slf4j
@RequestMapping(value = "/cookie", consumes = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@RestController
public class LogIn {
    @Autowired
    LogInService logInService;

    @Operation(summary = "Zapisujemy cookie po którym bedziemy sie autoryzować")
    @PostMapping(path = "/")
    ResponseEntity<String> postSaveCookie(@RequestBody TokenRequest request) {
        return logInService.logInToVinted(request);
    }
}
