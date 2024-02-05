package com.techelevator.tenmo.Service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.InsufficientBalanceException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component

public class TransferService {
    private TransferDao transferDao;
    private AccountDao accountDao;

    public TransferService(TransferDao transferDao, AccountDao accountDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
    }
    public void createSendTransfer(Transfer transfer) throws InsufficientBalanceException {
        transfer.setTransferTypeId(2);
        transfer.setTransferStatusId(2);
        if (updateBalances(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount())) {
            transferDao.createTransferSend(transfer);
        } else {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }
    public boolean updateBalances(int accountFrom, int accountTo, BigDecimal amount){
        if (amount.compareTo(BigDecimal.ZERO) < 0){
            System.out.println("the send amount must be greater than 0");
        }
        Account accountFromInfo = accountDao.getAccountByAccountId(accountFrom);
        Account accountToInfo = accountDao.getAccountByAccountId(accountTo);

        BigDecimal balanceFrom = accountFromInfo.getBalance();
        if (balanceFrom.compareTo(amount) >= 0){
            accountFromInfo.setBalance(balanceFrom.subtract(amount));
            accountToInfo.setBalance(accountToInfo.getBalance().add(amount));

            accountDao.updateBalances(accountFromInfo);
            accountDao.updateBalances(accountToInfo);
            return true;
        }else{
            return false;
        }
    }
    public void denyTransfer(int transferId) {
        transferDao.denyTransfer(transferId);
    }

    public void acceptTransfer(int transferId) {
        Transfer transfer = transferDao.getTransferById(transferId);
        transferDao.acceptTransfer(transferId);
        updateBalances(transfer.getAccountTo(),transfer.getAccountFrom(),transfer.getAmount());
    }
    public void createRequestTransfer(Transfer transfer){
        transfer.setTransferTypeId(1);
        transfer.setTransferStatusId(1);
        transferDao.createTransferRequest(transfer);
    }
}
