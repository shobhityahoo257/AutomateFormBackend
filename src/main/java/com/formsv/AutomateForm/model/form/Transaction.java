package com.formsv.AutomateForm.model.form;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String _id;
    private String userId;
    private String formId;
    private String transactionRefId;
    private String transactionId;
    private int totalPayment;
    private String transactionStatus;
    private String approvalRefNo;
}
