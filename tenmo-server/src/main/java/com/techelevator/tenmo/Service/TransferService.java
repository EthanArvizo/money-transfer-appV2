package com.techelevator.tenmo.Service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
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
    public String createSendTransfer(Transfer transfer){
        transfer.setTransferTypeId(2);
        transfer.setTransferStatusId(2);
        boolean success = updateBalances(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        if (success){
            transferDao.createTransfer(transfer);
            return "Transfer sucessful";
        }else{
            return "Transfer failed due to insufficient balance";
        }
    }
    public boolean updateBalances(int accountFrom, int accountTo, BigDecimal amount){
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
}
