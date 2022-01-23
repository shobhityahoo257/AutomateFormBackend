package com.formsv.AutomateForm.Constants;

public enum Constants {

    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    DELETED("DELETED"),
    TRANSACTIONID("X-TRANSACTION-ID");


    public final String value;
    private Constants(String value){
        this.value=value;
    }
}
