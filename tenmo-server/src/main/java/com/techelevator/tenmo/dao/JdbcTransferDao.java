package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component

public class JdbcTransferDao implements TransferDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

//    @Override
//    public void createTransfer(Transfer transfer) {
//        Transfer newTransfer = null;
//        String sql = "INSERT INTO transfer (transfer_type_id,transfer_status_id, account_from, account_to, amount)VALUES (?, ?, ?, ?, ?)";
//        try {
//            jdbcTemplate.update(sql,transfer.getTransferTypeId(), transfer.getTransferStatusId(),transfer.getAccountFrom(),transfer.getAccountTo(),transfer.getAmount());
//        }catch (CannotGetJdbcConnectionException e){
//            throw new DaoException("Unable to connect to server or database", e);
//        }
//    }

    @Override
    public Transfer getTransferById(int transferId) {
        Transfer transfer = null;
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,transferId);
            if (results.next()){
                transfer = mapRowToTransfer(results);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfer;
    }

    @Override
    public List<Transfer> getTransfersByAccountId(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from = ? OR account_to = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,accountId,accountId);
            while (results.next()){
                Transfer transfer = mapRowToTransfer(results);
                transfers.add(transfer);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }


    @Override
    public void createTransferRequest(Transfer transferRequest) {
        Transfer newTransfer = null;
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)"+
                "VALUES (?, ?, ?, ?, ?)";
        int transferTypeId = 1;
        int transferStatusId = 1;
           jdbcTemplate.update(sql,transferTypeId, transferStatusId, transferRequest.getAccountFrom(), transferRequest.getAccountTo(), transferRequest.getAmount());

    }

    @Override
    public void createTransferSend(Transfer transferSend) {
        Transfer newTransfer = null;
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)"+
                "VALUES (?, ?, ?, ?, ?)";
        int transferTypeId = 2;
        int transferStatusId = 2;
            jdbcTemplate.update(sql,transferTypeId, transferStatusId, transferSend.getAccountFrom(), transferSend.getAccountTo(), transferSend.getAmount());
    }

    @Override
    public List<Transfer> getPendingTransfersByAccountId(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_to = ? AND transfer_status_id = 1";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,accountId);
            while (results.next()){
                Transfer transfer = mapRowToTransfer(results);
                transfers.add(transfer);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }

    @Override
    public void denyTransfer(int transferId) {
        String sql = "UPDATE transfer SET transfer_status_id = 3 WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transferId);
    }

    @Override
    public void acceptTransfer(int transferId) {
        String sql = "UPDATE transfer SET transfer_status_id = 2 WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transferId);
    }

    private Transfer mapRowToTransfer(SqlRowSet rs){
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }
}
