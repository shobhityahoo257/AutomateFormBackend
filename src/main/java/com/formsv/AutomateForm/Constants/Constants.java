package com.formsv.AutomateForm.Constants;

public enum Constants {

    PENDING("PENDING"),
    COMPLETED("COMPLETED");


    public final String value;
    private Constants(String value){
        this.value=value;
    }
}
