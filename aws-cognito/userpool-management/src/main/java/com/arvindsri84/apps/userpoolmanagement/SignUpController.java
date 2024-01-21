package com.arvindsri84.apps.userpoolmanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;

@RestController
@RequestMapping("signup")
@CrossOrigin("*")
public class SignUpController {

    private static final Logger log = LoggerFactory.getLogger(SignUpController.class);

    @Value("${aws.cognito.userpoolId}")
    private String userPoolId;

    @GetMapping("/confirm/{username}")
    public void confirm(@PathVariable String username) {
        log.info("clientId: {}  username: {}", userPoolId, username);
        try (var identityProviderClient =
                     CognitoIdentityProviderClient.builder().region(Region.AP_SOUTH_1).build()) {

            var req = AdminConfirmSignUpRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .build();
            identityProviderClient.adminConfirmSignUp(req);
        }catch (UserNotFoundException uex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,uex.getMessage());
        }
    }
}
