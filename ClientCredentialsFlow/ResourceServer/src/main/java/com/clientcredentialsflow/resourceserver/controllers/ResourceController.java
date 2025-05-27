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
                  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                  Jwt jwt = (Jwt) authentication.getPrincipal();

                  Map<String, Object> response = new HashMap<>();
                  response.put("title", "Protected Resource Access");
                  response.put("description",
                              "This endpoint returns JWT token and client metadata accessible with valid credentials.");
                  response.put("resource_id", "resource-123");
                  response.put("accessed_at", ZonedDateTime.now(ZoneId.systemDefault()).toString());

                  // Token Metadata
                  Map<String, Object> tokenInfo = new HashMap<>();
                  tokenInfo.put("token_id", jwt.getId());
                  tokenInfo.put("subject", jwt.getSubject());
                  tokenInfo.put("client_id", jwt.getClaim("azp"));
                  tokenInfo.put("issuer", jwt.getIssuer().toString());
                  tokenInfo.put("audience", jwt.getAudience());
                  tokenInfo.put("token_type", jwt.getClaimAsString("typ"));
                  tokenInfo.put("scopes", jwt.getClaimAsString("scope"));

                  Instant issuedAt = jwt.getIssuedAt();
                  Instant expiresAt = jwt.getExpiresAt();

                  if (issuedAt != null) {
                        tokenInfo.put("issued_at", Map.of(
                                    "raw", issuedAt.toString(),
                                    "human", ZonedDateTime.ofInstant(issuedAt, ZoneId.systemDefault()).toString(),
                                    "timestamp", issuedAt.getEpochSecond()));
                  }

                  if (expiresAt != null) {
                        long validForSeconds = expiresAt.getEpochSecond() - Instant.now().getEpochSecond();
                        tokenInfo.put("expires_at", Map.of(
                                    "raw", expiresAt.toString(),
                                    "human", ZonedDateTime.ofInstant(expiresAt, ZoneId.systemDefault()).toString(),
                                    "timestamp", expiresAt.getEpochSecond(),
                                    "valid_for_seconds", validForSeconds));
                  }

                  // Client & Token Creation Info
                  Map<String, Object> clientInfo = new HashMap<>();
                  clientInfo.put("auth_method", "Client Credentials Flow");
                  clientInfo.put("grant_type", "client_credentials");
                  clientInfo.put("authentication_type", "Client ID + Secret");
                  clientInfo.put("token_endpoint", jwt.getIssuer() + "/protocol/openid-connect/token");
                  clientInfo.put("preferred_username", jwt.getClaimAsString("preferred_username"));
                  clientInfo.put("client_host", jwt.getClaimAsString("clientHost"));
                  clientInfo.put("client_address", jwt.getClaimAsString("clientAddress"));

                  // Realm and Resource Roles
                  if (jwt.getClaim("realm_access") != null) {
                        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
                        clientInfo.put("realm_roles", realmAccess.get("roles"));
                  }

                  if (jwt.getClaim("resource_access") != null) {
                        clientInfo.put("resource_access", jwt.getClaim("resource_access"));
                  }

                  // Business Data (for demo)
                  Map<String, Object> data = new HashMap<>();
                  data.put("plan", "Premium");
                  data.put("quota_limit", 1000);
                  data.put("features",
                              new String[] { "3D Viewer Access", "High Priority Support", "Analytics Dashboard" });

                  // Final structure
                  response.put("token_metadata", tokenInfo);
                  response.put("client_info", clientInfo);
                  response.put("data", data);

                  System.out.println("Protected response created");
                  return response;
            } catch (Exception e) {
                  System.err.println("Error in protected resource: " + e.getMessage());
                  e.printStackTrace();

                  Map<String, Object> errorResponse = new HashMap<>();
                  errorResponse.put("error", "Unable to process protected resource");
                  errorResponse.put("message", e.getMessage());
                  return errorResponse;
            }
      }

}