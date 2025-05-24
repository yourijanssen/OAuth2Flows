package com.oauth2flows.authorizationcodeflow.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

      // Google user endpoint - consistent with Facebook endpoint
      @GetMapping("/google/user")
      public ResponseEntity<?> getGoogleUser(@AuthenticationPrincipal OAuth2User principal) {
            if (principal == null) {
                  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("name", principal.getAttribute("name"));
            userDetails.put("email", principal.getAttribute("email"));
            userDetails.put("picture", principal.getAttribute("picture"));
            userDetails.put("provider", "Google");

            return ResponseEntity.ok(userDetails);
      }

      // Facebook user endpoint
      @GetMapping("/facebook/user")
      public ResponseEntity<?> getFacebookUser(@AuthenticationPrincipal OAuth2User principal) {
            if (principal == null) {
                  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> userDetails = new HashMap<>();

            // Extract basic attributes
            userDetails.put("name", principal.getAttribute("name"));
            userDetails.put("email", principal.getAttribute("email"));

            // Extract Facebook's nested picture structure
            Object pictureObj = principal.getAttribute("picture");
            String pictureUrl = null;

            if (pictureObj instanceof Map) {
                  // Facebook format: picture -> data -> url
                  @SuppressWarnings("unchecked")
                  Map<String, Object> pictureMap = (Map<String, Object>) pictureObj;
                  if (pictureMap.containsKey("data")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> dataMap = (Map<String, Object>) pictureMap.get("data");
                        pictureUrl = (String) dataMap.get("url");
                  }
            }

            userDetails.put("picture", pictureUrl);
            userDetails.put("provider", "Facebook");

            return ResponseEntity.ok(userDetails);
      }

      // Universal endpoint that works for both providers
      @GetMapping("/auth/user")
      public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
            if (principal == null) {
                  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> userDetails = new HashMap<>();

            // Extract common attributes
            userDetails.put("name", principal.getAttribute("name"));
            userDetails.put("email", principal.getAttribute("email"));

            // Determine provider and extract picture
            String provider;
            String pictureUrl = null;

            // Check if this is a Google user (has 'sub' attribute)
            if (principal.getAttributes().containsKey("sub")) {
                  provider = "Google";
                  pictureUrl = principal.getAttribute("picture");
            }
            // Check if this is a Facebook user (has 'id' attribute)
            else if (principal.getAttributes().containsKey("id")) {
                  provider = "Facebook";

                  // Extract Facebook's nested picture structure
                  Object pictureObj = principal.getAttribute("picture");
                  if (pictureObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> pictureMap = (Map<String, Object>) pictureObj;
                        if (pictureMap.containsKey("data")) {
                              @SuppressWarnings("unchecked")
                              Map<String, Object> dataMap = (Map<String, Object>) pictureMap.get("data");
                              pictureUrl = (String) dataMap.get("url");
                        }
                  }
            } else {
                  provider = "Unknown";
            }

            userDetails.put("picture", pictureUrl);
            userDetails.put("provider", provider);

            return ResponseEntity.ok(userDetails);
      }
}