package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class TransferService {
    private final String API_TRANSFER_URL = "http://localhost:8080/transfer";
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();
    public List<Transfer> getTransferByAccountId(String token, int accountId){
        String endpoint = "/account/{accountId}";
        return makeAuthenticatedGetRequest(token,endpoint,Transfer[].class,accountId);
    }
    public void displayTransfers(List<Transfer> transfers, int accountId, String token) {
        if (transfers == null || transfers.isEmpty()) {
            System.out.println("No past transfer history");
        } else {
            System.out.println("Transfers");
            System.out.println("ID\t\tFrom/To\t\t\tAmount");
            System.out.println("-------------------------------------------");
            for (Transfer transfer : transfers) {
                int fromUserId = accountService.getUserIdByAccountId(token, transfer.getAccountFrom());
                int toUserId = accountService.getUserIdByAccountId(token, transfer.getAccountTo());
                String fromTo;
                if (fromUserId == accountId) {
                    fromTo = String.format("From: %s  To: %s", getUserUsernameById(fromUserId, token), getUserUsernameById(toUserId, token));
                } else {
                    fromTo = String.format("From: %s  To: %s", getUserUsernameById(toUserId, token), getUserUsernameById(fromUserId, token));
                }
                String amount = String.format("$ %.2f", transfer.getAmount());
                System.out.printf("%-10d%-25s%-10s%n", transfer.getTransferId(), fromTo, amount);
            }
            System.out.println("-------------------------------------------");
            System.out.print("Please enter transfer ID to view details (0 to cancel): ");
        }
    }
    private String getUserUsernameById(int id,String token) {
        return userService.getUserByUserId(token,id).getUsername();
    }
    public <T> List<T> makeAuthenticatedGetRequest(String token, String endpoint, Class<T[]> responseType, Object... uriVariables) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<T[]> responseEntity = restTemplate.exchange(
                API_TRANSFER_URL + endpoint,
                HttpMethod.GET,
                entity,
                responseType,
                uriVariables
        );

        return Arrays.asList(responseEntity.getBody());
    }



}
