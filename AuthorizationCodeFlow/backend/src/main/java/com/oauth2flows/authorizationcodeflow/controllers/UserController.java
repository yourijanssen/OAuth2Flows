package com.oauth2flows.authorizationcodeflow.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

      @GetMapping("/google/user")
      public Map<String, Object> getUser(@AuthenticationPrincipal OAuth2User principal) {
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("name", principal.getAttribute("name"));
            userDetails.put("email", principal.getAttribute("email"));
            userDetails.put("picture", principal.getAttribute("picture"));
            return userDetails;
      }

      @GetMapping("/facebook/user")
      public ResponseEntity<?> getAuthStatus(@AuthenticationPrincipal OAuth2User principal) {
            if (principal == null) {
                  return ResponseEntity.ok(Map.of("authenticated", false));
            }

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("authenticated", true);

            // Handle different attribute structures from Google vs Facebook
            String name = principal.getAttribute("name");
            String email = principal.getAttribute("email");

            // For Facebook, picture is nested inside a 'picture' object
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
            } else if (pictureObj instanceof String) {
                  // Google format: picture is a direct URL
                  pictureUrl = (String) pictureObj;
            }

            userInfo.put("name", name);
            userInfo.put("email", email);
            userInfo.put("picture", pictureUrl);
            userInfo.put("provider", principal.getAttributes().containsKey("sub") ? "google" : "facebook");

            return ResponseEntity.ok(userInfo);
      }
}