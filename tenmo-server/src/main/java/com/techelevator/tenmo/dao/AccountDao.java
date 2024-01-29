package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDao  {
    Account getAccountById(int id);
    Account getAccountByAccountId(int accountId);
    void updateBalances(Account account);

}
