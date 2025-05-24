package com.clientcredentialsflow.resourceserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api")
public class ResourceController {

      @GetMapping("/status/check")
      public Map<String, Object> status() {
            System.out.println("Status endpoint called");

            Map<String, Object> response = new HashMap<>();
            response.put("status", "up");
            response.put("message", "Resource Server called");
            response.put("timestamp", new Date().toString());
            response.put("serverInfo", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            System.out.println(response);
            return response;
      }

      @GetMapping("/protected-resource")
      @PreAuthorize("hasAuthority('SCOPE_email') or hasAuthority('SCOPE_profile')")
      public Map<String, Object> protectedResource() {
            System.out.println("Protected resource endpoint called");

            try {
                  // Get authentication details from the token
                  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                  Jwt jwt = (Jwt) authentication.getPrincipal();

                  System.out.println("JWT claims: " + jwt.getClaims());
                  System.out.println("JWT scopes: " + jwt.getClaimAsString("scope"));

                  Map<String, Object> response = new HashMap<>();
                  response.put("message", "This is protected data - accessible only with valid JWT");
                  response.put("access_notice",
                              "This endpoint can only be accessed by clients registered in Keycloak with proper credentials");
                  response.put("resourceId", "resource-123");
                  response.put("timestamp", new Date().toString());

                  // Add JWT token information
                  Map<String, Object> tokenInfo = new HashMap<>();
                  tokenInfo.put("token_id", jwt.getId());
                  tokenInfo.put("subject", jwt.getSubject());
                  tokenInfo.put("client_id", jwt.getClaim("azp"));
                  tokenInfo.put("issuer", jwt.getIssuer().toString());
                  tokenInfo.put("audience", jwt.getAudience());

                  // Format timestamps for readability
                  Instant issuedAt = jwt.getIssuedAt();
                  Instant expiresAt = jwt.getExpiresAt();

                  if (issuedAt != null) {
                        ZonedDateTime issuedDateTime = ZonedDateTime.ofInstant(issuedAt, ZoneId.systemDefault());
                        tokenInfo.put("issued_at", issuedDateTime.toString());
                        tokenInfo.put("issued_at_timestamp", issuedAt.getEpochSecond());
                  }

                  if (expiresAt != null) {
                        ZonedDateTime expiresDateTime = ZonedDateTime.ofInstant(expiresAt, ZoneId.systemDefault());
                        tokenInfo.put("expires_at", expiresDateTime.toString());
                        tokenInfo.put("expires_at_timestamp", expiresAt.getEpochSecond());

                        long validForSeconds = expiresAt.getEpochSecond() - Instant.now().getEpochSecond();
                        tokenInfo.put("valid_for_seconds", validForSeconds);
                  }

                  tokenInfo.put("token_type", jwt.getClaimAsString("typ"));
                  tokenInfo.put("scopes", jwt.getClaimAsString("scope"));

                  // Include information about how the token was created
                  Map<String, Object> tokenCreationInfo = new HashMap<>();
                  tokenCreationInfo.put("auth_method", "Client Credentials Flow");
                  tokenCreationInfo.put("grant_type", "client_credentials");
                  tokenCreationInfo.put("client_authentication", "Client ID + Client Secret");
                  tokenCreationInfo.put("token_endpoint", jwt.getIssuer() + "/protocol/openid-connect/token");
                  tokenCreationInfo.put("username", jwt.getClaimAsString("preferred_username"));
                  tokenCreationInfo.put("client_host", jwt.getClaimAsString("clientHost"));
                  tokenCreationInfo.put("client_address", jwt.getClaimAsString("clientAddress"));

                  // Add realm roles if present
                  if (jwt.getClaim("realm_access") != null) {
                        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
                        tokenCreationInfo.put("realm_roles", realmAccess.get("roles"));
                  }

                  // Add resource access if present
                  if (jwt.getClaim("resource_access") != null) {
                        tokenCreationInfo.put("resource_access", jwt.getClaim("resource_access"));
                  }

                  response.put("token_info", tokenInfo);
                  response.put("token_creation", tokenCreationInfo);

                  // Include additional data for demonstration
                  Map<String, Object> sensitiveData = new HashMap<>();
                  sensitiveData.put("apiKey", "sk_test_protected_data_123456");
                  sensitiveData.put("quota", 1000);
                  sensitiveData.put("tier", "premium");

                  response.put("data", sensitiveData);

                  System.out.println("Protected response created");
                  return response;
            } catch (Exception e) {
                  System.err.println("Error in protected resource: " + e.getMessage());
                  e.printStackTrace();

                  Map<String, Object> errorResponse = new HashMap<>();
                  errorResponse.put("error", "Error processing protected resource");
                  errorResponse.put("message", e.getMessage());
                  return errorResponse;
            }
      }
}