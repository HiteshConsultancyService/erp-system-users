package com.erpsystem.users.controller.authentication;

import com.erpsystem.users.entity.Role;
import com.erpsystem.users.entity.User;
import com.erpsystem.users.service.RoleService;
import com.erpsystem.users.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthentication {

    @Value("${spring.oauth.credentials.token_endpoint}")
    String tokenEndpoint;

    @Value("${spring.oauth.credentials.user_info_endpoint}")
    String userInfoEndpoint;

    @Value("${spring.oauth.credentials.user_info_endpoint_v2}")
    String userInfoEndpointv2;

    @Value("${spring.oauth.credentials.client_id}")
    String clientId;

    @Value("${spring.oauth.credentials.client_secret}")
    String clientSecret;

    @Value("${spring.oauth.credentials.redirect_uri}")
    String redirectUri;

    @Value("${spring.oauth.credentials.grant_type}")
    String grantType;

    RestTemplate restTemplate;

    UserService userService;

    RoleService roleService;

    GoogleAuthentication(RestTemplate restTemplate, UserService userService, RoleService roleService) {
        this.restTemplate = restTemplate;
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {

        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("grant_type", grantType);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, httpEntity, Map.class);

            assert response.getBody() != null;
            String accessToken = (String) response.getBody().get("access_token");
            String refreshToken = (String) response.getBody().get("refresh_token");

            headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            httpEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoEndpointv2, HttpMethod.GET, httpEntity, Map.class);

            Map userInfo = userInfoResponse.getBody();

            assert userInfo != null;
            if(userService.getUserByEmail(userInfo.get("email").toString()) == null) {
                User user = new User();
                user.setUsername(userInfo.get("name").toString());
                user.setEmail(userInfo.get("email").toString());
                user.setRegistrationType("OAUTH");
                Role role = roleService.getRole(2);
                user.setRole(role);
                userService.create(user);
            }
            userInfo.put("access_token", accessToken);
            userInfo.put("refresh_token", refreshToken);
            return ResponseEntity.status(HttpStatus.OK).body(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> getAccessToken(@RequestParam String refreshToken) {

        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
            params.add("refresh_token", refreshToken);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("grant_type", "refresh_token");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, httpEntity, Map.class);

            Map body = response.getBody();

            return ResponseEntity.status(HttpStatus.OK).body(body);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }
}
