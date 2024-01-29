package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/account")

public class AccountController {
    private final AccountDao accountDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }


    @GetMapping("/{userId}")
    public Account getAccountByUserId(@PathVariable int userId){
        return accountDao.getAccountById(userId);
    }
    @GetMapping("/byAccount/{accountId}")
    public Account getAccountByAccountId(@PathVariable int accountId){
        return accountDao.getAccountByAccountId(accountId);
    }
}
