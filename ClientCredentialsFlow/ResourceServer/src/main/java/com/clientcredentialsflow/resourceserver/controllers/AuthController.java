package com.clientcredentialsflow.resourceserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

      @Value("${keycloak.auth-server-url}")
      private String keycloakUrl;

      @Value("${keycloak.realm}")
      private String realm;

      @Value("${keycloak.client-id}")
      private String clientId;

      @Value("${keycloak.client-secret}")
      private String clientSecret;

      @PostMapping("/token")
      public ResponseEntity<String> getToken() {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "client_credentials");
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            ResponseEntity<String> response = restTemplate.postForEntity(
                        tokenUrl,
                        request,
                        String.class);

            return response;
      }
}