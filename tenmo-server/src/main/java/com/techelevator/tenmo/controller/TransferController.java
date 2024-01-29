package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/transfer")

public class TransferController {
    private final TransferDao transferDao;

    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }
    @PostMapping("/create")
    public String createTransfer(@Valid @RequestBody Transfer newTransfer){
        Transfer transfer = transferDao.createTransfer(newTransfer);
        return "Transfer created successfully";
    }
    @GetMapping("/{transferId}")
    public Transfer getTransferByTransferId(@PathVariable int transferId){
        return transferDao.getTransferById(transferId);
    }
    @GetMapping("/account/{accountId}")
    public List<Transfer> getTransfersByAccountId(@PathVariable int accountId){
        return transferDao.getTransfersByAccountId(accountId);
    }
    private void sendTransfer() {
        Transfer transfer = new Transfer();
        transfer.setTransferTypeId(2);
        transfer.setTransferStatusId(2);
    }
}
