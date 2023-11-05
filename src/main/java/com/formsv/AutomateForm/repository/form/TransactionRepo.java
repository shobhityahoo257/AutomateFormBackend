package com.formsv.AutomateForm.repository.form;


import com.formsv.AutomateForm.model.form.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends MongoRepository<Transaction,String> {

}
