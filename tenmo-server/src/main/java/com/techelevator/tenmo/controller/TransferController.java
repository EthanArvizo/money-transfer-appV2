package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Service.TransferService;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/transfer")

public class TransferController {
    private final TransferDao transferDao;
    private final TransferService transferService;



    public TransferController(TransferDao transferDao, TransferService transferService) {
        this.transferDao = transferDao;
        this.transferService = transferService;
    }
//    @PostMapping("/create")
//    @ResponseStatus(HttpStatus.CREATED)
//    public String createTransfer(@Valid @RequestBody Transfer newTransfer){
//        transferDao.createTransfer(newTransfer);
//        return "Transfer created successfully";
//    }
    @GetMapping("/{transferId}")
    public Transfer getTransferByTransferId(@PathVariable int transferId){
        return transferDao.getTransferById(transferId);
    }
    @GetMapping("/account/{accountId}")
    public List<Transfer> getTransfersByAccountId(@PathVariable int accountId){
        return transferDao.getTransfersByAccountId(accountId);
    }
    @PostMapping("/request")
    public String createRequestTransfer(@Valid @RequestBody Transfer newTransfer){
        transferDao.createTransferRequest(newTransfer);
        return "Transfer created successfully";
    }
    @PostMapping("/send")
    public String createSendTransfer(@Valid @RequestBody Transfer newTransfer){
        transferService.createSendTransfer(newTransfer);
        return "Transfer created successfully";
    }

}
