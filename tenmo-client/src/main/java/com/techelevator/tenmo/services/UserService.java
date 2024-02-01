package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class UserService {
    private final String API_USER_URL = "http://localhost:8080/user";
    private final RestTemplate restTemplate = new RestTemplate();
    private final AccountService accountService = new AccountService();
    public User getUserByUserId(String token, int userId){
        String endpoint = "/{userId}";
        return makeAuthenticatedGetRequest(token,endpoint,User.class,userId);
    }
    public int getUserIdByAccountId(String token, int accountId){
        return accountService.getAccountByUserId(token,accountId).getUserId();
    }

    public <T> T makeAuthenticatedGetRequest(String token, String endpoint, Class<T> responseType, Object... uriVariables) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<T> response = restTemplate.exchange(
                API_USER_URL + endpoint,
                HttpMethod.GET,
                entity,
                responseType,
                uriVariables
        );

        return response.getBody();
    }
}
