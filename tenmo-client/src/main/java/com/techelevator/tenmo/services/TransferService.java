package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dto.TransferRequest;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLoggerException;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TransferService {
    private final String API_TRANSFER_URL = "http://localhost:8080/transfer";
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();
    public List<Transfer> getTransferByAccountId(String token, int accountId){
        String endpoint = "/account/{accountId}";
        return makeAuthenticatedGetRequest(token,endpoint,Transfer[].class,accountId);
    }
    private String getUserUsernameById(int id,String token) {
        return userService.getUserByUserId(token,id).getUsername();
    }
    public List<Transfer> getPendingTransfers(String token, int accountId){
        String endpoint = "/account/{accountId}/pending";
        return makeAuthenticatedGetRequest(token,endpoint,Transfer[].class,accountId);
    }
    public void denyTransfer(String token, int transferId){
        String endpoint = "/deny/{transferId}";

        HttpHeaders httpHeaders = createAuthenticatedHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);
        try {
            restTemplate.postForEntity(API_TRANSFER_URL + endpoint, entity, Void.class, transferId);
            System.out.println("Transfer denied successfully!");
        } catch (Exception e) {
            System.out.println("An error occurred while denying the transfer: " + e.getMessage());
        }
    }
    public void acceptTransfer(String token, int transferId){
        String endpoint = "/accept/{transferId}";
        HttpHeaders httpHeaders = createAuthenticatedHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);
        try {
            restTemplate.postForEntity(API_TRANSFER_URL + endpoint, entity, Void.class, transferId);
            System.out.println("Transfer accepted successfully!");
        } catch (Exception e) {
            System.out.println("An error occurred while denying the transfer: " + e.getMessage());
        }
    }

    public void createSendTransfer(String token, int accountFrom, int accountTo, BigDecimal amount) {
        String endpoint = "/send";

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountFrom(accountFrom);
        transferRequest.setAccountTo(accountTo);
        transferRequest.setAmount(amount);
        try {
            makeAuthenticatedPostRequest(token, endpoint, transferRequest);
            System.out.println("Transfer successful!");
        } catch (Exception e) {
            if (e.getMessage().contains("Insufficient balance")) {
                System.out.println("Transfer failed: Insufficient balance in the sender's account.");
            } else {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
    public void createRequestTransfer(String token, int accountFrom, int accountTo, BigDecimal amount){
        String endpoint = "/request";

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountFrom(accountFrom);
        transferRequest.setAccountTo(accountTo);
        transferRequest.setAmount(amount);

        makeAuthenticatedPostRequest(token,endpoint,transferRequest);
        System.out.println("Request successfully created");
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
                String fromTo = "";
                if (fromUserId == accountId) {
                    fromTo = String.format("From: %s  To: %s", getUserUsernameById(fromUserId, token), getUserUsernameById(toUserId, token));
                } else {
                    fromTo = String.format("From: %s  To: %s", getUserUsernameById(toUserId, token), getUserUsernameById(fromUserId, token));
                }
                String amount = String.format("$ %.2f", transfer.getAmount());
                System.out.printf("%-10d%-25s%-10s%n", transfer.getTransferId(), fromTo, amount);
            }
            System.out.println("-------------------------------------------");
        }
    }
    public void processTransferDetails(int transferId, List<Transfer> transfers, String token) {
        Transfer selectedTransfer = null;
        for (Transfer transfer : transfers) {
            if (transfer.getTransferId() == transferId) {
                selectedTransfer = transfer;
                break;
            }
        }
        if (selectedTransfer != null) {
            System.out.println("--------------------------------------------");
            System.out.println("Transfer Details");
            System.out.println("--------------------------------------------");
            System.out.println(" Id: " + selectedTransfer.getTransferId());
            int fromUserId = accountService.getUserIdByAccountId(token, selectedTransfer.getAccountFrom());
            int toUserId = accountService.getUserIdByAccountId(token, selectedTransfer.getAccountTo());
            System.out.println(" From: " + getUserUsernameById(fromUserId, token));
            System.out.println(" To: " + getUserUsernameById(toUserId, token));
            System.out.println(" Type: " + getTransferType(selectedTransfer.getTransferTypeId()));
            System.out.println(" Status: " + getTransferStatus(selectedTransfer.getTransferStatusId()));
            System.out.println(" Amount: $" + selectedTransfer.getAmount());
            System.out.println("--------------------------------------------");
        } else {
            System.out.println("Transfer with ID " + transferId + " not found");
        }
    }
    public void displayPendingRequests(List<Transfer> transfers, String token) {
        if (transfers == null || transfers.isEmpty()) {
            System.out.println("You currently have no pending transfers");
        } else {
            System.out.println("-------------------------------------------");
            System.out.println("Current Pending Transfers");
            System.out.println("ID\t\tFrom\t\t\t\tAmount");
            for (Transfer transfer : transfers) {
                int fromUserId = accountService.getUserIdByAccountId(token, transfer.getAccountFrom());
                String formattedAmount = String.format("$ %.2f", transfer.getAmount());
                System.out.printf("%-8d%-20s%-15s%n", transfer.getTransferId(), getUserUsernameById(fromUserId, token), formattedAmount);
            }
            System.out.println("-------------------------------------------");
        }
    }
    private String getTransferType(int transferTypeId) {
        if (transferTypeId == 1) {
            return "Request";
        } else {
            return "Send";
        }
    }
    private String getTransferStatus(int transferStatusId) {
        if (transferStatusId == 1) {
            return "Pending";
        } else if (transferStatusId == 2) {
            return "Approved";
        } else {
            return "Rejected";
        }
    }
    private HttpHeaders createAuthenticatedHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + token);
        return httpHeaders;
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
    public void makeAuthenticatedPostRequest(String token, String endpoint, Object requestObject) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + token);

        HttpEntity<Object> entity = new HttpEntity<>(requestObject, httpHeaders);

        restTemplate.exchange(
                API_TRANSFER_URL + endpoint,
                HttpMethod.POST,
                entity,
                Void.class
        );
    }


}
