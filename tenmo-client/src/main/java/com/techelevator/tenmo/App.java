package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final TransferService transferService = new TransferService();
    private final UserService userService = new UserService();

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }
    private void handleApprovalOrRejection(int transferId) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please choose an option: ");

        int userChoice = scanner.nextInt();

        if (userChoice == 1) {
           transferService.acceptTransfer(currentUser.getToken(),currentUser.getUser().getId(),transferId);
        } else if (userChoice == 2) {
            transferService.denyTransfer(currentUser.getToken(),transferId);
        } else {
            System.out.println("Operation canceled.");
        }

    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        System.out.println("Current Balance: $" + accountService.getBalanceByUserId(currentUser.getToken(), currentUser.getUser().getId()));
	}

	private void viewTransferHistory() {
        int userId = currentUser.getUser().getId();
        Account account = accountService.getAccountByUserId(currentUser.getToken(), userId);
        int accountId = account.getAccountId();

        List<Transfer> transferList = transferService.getTransferByAccountId(currentUser.getToken(), accountId);
        boolean hasTransfers = transferService.displayTransfers(transferList, accountId, currentUser.getToken());
        if (!hasTransfers) {
            return;
        }
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter transfer ID to view details (0 to cancel): ");
            int transferId = scanner.nextInt();
            if (transferId == 0){
                return;
            }
            transferService.processTransferDetails(transferId, transferList, currentUser.getToken());
        }catch (InputMismatchException e){
            System.out.println("Please enter a proper ID value");
        }



    }


	private void viewPendingRequests() {
        int userId = currentUser.getUser().getId();
        Account account = accountService.getAccountByUserId(currentUser.getToken(), userId);
        int accountId = account.getAccountId();

        List<Transfer> transferList = transferService.getPendingTransfers(currentUser.getToken(), accountId);
        boolean hasPendingTransfers = transferService.displayPendingRequests(transferList, currentUser.getToken());

        if (!hasPendingTransfers) {
            return;
        }
        try {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter transfer ID to view details (0 to cancel): ");
        int transferId = scanner.nextInt();
            if (transferId == 0){
                return;
            }


        transferService.processTransferDetails(transferId, transferList, currentUser.getToken());
        consoleService.printPendingRequestOptions();
        handleApprovalOrRejection(transferId);
    }catch (InputMismatchException e){
            System.out.println("Please enter a proper ID value");
        }
	}

	private void sendBucks() {
        Scanner scanner = new Scanner(System.in);

        int userId = currentUser.getUser().getId();
        Account account = accountService.getAccountByUserId(currentUser.getToken(), userId);

        int accountFrom = account.getAccountId();
        List<User> users = userService.getUsers(currentUser.getToken(), currentUser.getUser().getUsername());
        userService.displayUsers(users);
        try {
            System.out.println("Enter the user ID of who you want to send the transfer to (0 to cancel): ");
            int userToId = scanner.nextInt();
            if (userToId == 0){
                return;
            }

            if (userToId == userId) {
                System.out.println("Cannot send money to your own account. Operation canceled.");
                return;
            }

            Account account2 = accountService.getAccountByUserId(currentUser.getToken(), userToId);
            int accountTo = account2.getAccountId();

            System.out.println("Enter the amount you want to send in decimal form (0 to cancel): ");
            BigDecimal moneyAmount = scanner.nextBigDecimal();
            if (moneyAmount.compareTo(BigDecimal.ZERO) == 0){
                return;
            }

            if (moneyAmount.compareTo(BigDecimal.ZERO) <= -1) {
                System.out.println("Transfer amount must be a positive value. Transfer canceled.");
                return;
            }
            transferService.createSendTransfer(currentUser.getToken(), accountFrom, accountTo, moneyAmount);
        }catch (InputMismatchException | NullPointerException e){
            System.out.println("Please enter a proper value for the request");
        }
    }

	private void requestBucks() {
        Scanner scanner = new Scanner(System.in);

        int userId = currentUser.getUser().getId();
        Account account = accountService.getAccountByUserId(currentUser.getToken(), userId);

        int accountFrom = account.getAccountId();
        List<User> users = userService.getUsers(currentUser.getToken(), currentUser.getUser().getUsername());
        userService.displayUsers(users);
        try {
            System.out.println("Enter the user ID of who you want to request the transfer to (0 to cancel): ");
            int userToId = scanner.nextInt();

            if (userToId == 0){
                return;
            }

            if (userToId == userId) {
                System.out.println("Cannot send money to your own account. Operation canceled.");
                return;
            }

            Account account2 = accountService.getAccountByUserId(currentUser.getToken(), userToId);
            int accountTo = account2.getAccountId();


            System.out.println("Enter the amount you want to send in decimal form (0 to cancel): ");
            BigDecimal moneyAmount = scanner.nextBigDecimal();
            if (moneyAmount.compareTo(BigDecimal.ZERO) == 0){
                return;
            }
            if (moneyAmount.compareTo(BigDecimal.ZERO) <= -1) {
                System.out.println("Transfer amount must be a positive value. Transfer canceled.");
                return;
            }
            transferService.createRequestTransfer(currentUser.getToken(), accountFrom, accountTo, moneyAmount);



        } catch (InputMismatchException | NullPointerException e) {
            System.out.println("Please enter a proper value for the request");

        }
    }

}
