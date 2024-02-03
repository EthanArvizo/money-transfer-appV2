package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {
    //void createTransfer(Transfer transfer);
    Transfer getTransferById(int transferId);
    List<Transfer> getTransfersByAccountId(int accountId);
//    List<Transfer> getTransfersByUserId(int userId);
    void createTransferRequest(Transfer transferRequest);
    void createTransferSend(Transfer transferSend);
}
