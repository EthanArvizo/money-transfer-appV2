package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;

public class AccountService {
    private final String API_ACCOUNT_URL = "http://localhost:8080/account";
    private final RestTemplate restTemplate = new RestTemplate();
    public Account getAccountByUserId(String token, int userId){
        String endpoint = "/{userId}";
        System.out.println("Requesting Account for userId: " + userId);
        return makeAuthenticatedGetRequest(token,endpoint, Account.class,userId);
    }
    public BigDecimal getBalanceByUserId(String token, int userId){
        String endpoint = "/{userId}";
        Account account = makeAuthenticatedGetRequest(token,endpoint,Account.class,userId);
        return account.getBalance();

    }
    public <T> T makeAuthenticatedGetRequest(String token, String endpoint, Class<T> responseType, Object... uriVariables) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<T> response = restTemplate.exchange(
                API_ACCOUNT_URL + endpoint,
                HttpMethod.GET,
                entity,
                responseType,
                uriVariables
        );

        return response.getBody();
    }
}
