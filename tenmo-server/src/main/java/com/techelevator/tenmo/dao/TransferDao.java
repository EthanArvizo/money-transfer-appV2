package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {
    Transfer getTransferById(int transferId);
    List<Transfer> getTransfersByAccountId(int accountId);
    void createTransferRequest(Transfer transferRequest);
    void createTransferSend(Transfer transferSend);
    List<Transfer> getPendingTransfersByAccountId(int accountId);
    void denyTransfer(int transferId);
    void acceptTransfer(int transferId);
}
