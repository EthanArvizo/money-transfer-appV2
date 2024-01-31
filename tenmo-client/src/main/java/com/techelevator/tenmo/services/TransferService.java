package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class TransferService {
    private final String API_TRANSFER_URL = "http://localhost:8080/transfer";
    private final RestTemplate restTemplate = new RestTemplate();
//    public Transfer getTransferByUserId(String token, int userId){
//        String endpoint
//    }

    public <T> T makeAuthenticatedGetRequest(String token, String endpoint, Class<T> responseType, Object... uriVariables) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<T> response = restTemplate.exchange(
                API_TRANSFER_URL + endpoint,
                HttpMethod.GET,
                entity,
                responseType,
                uriVariables
        );

        return response.getBody();
    }
}
