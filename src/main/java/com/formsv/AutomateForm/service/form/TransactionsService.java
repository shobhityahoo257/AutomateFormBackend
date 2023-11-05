package com.formsv.AutomateForm.service.form;

import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.Transaction;
import com.formsv.AutomateForm.repository.form.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TransactionsService {
    @Autowired
    TransactionRepo transactionRepo;

    public ResponseEntity createTransaction(Transaction transaction){
       try{
           return new ResponseEntity(transactionRepo.save(transaction), HttpStatus.CREATED);
       }
       catch (Exception e)
       {
           return new ResponseEntity(ExceptionConstants.DATAALREADYEXIST.value, HttpStatus.BAD_REQUEST);
       }

    }
}
