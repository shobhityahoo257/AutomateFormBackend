package com.formsv.AutomateForm.Constants;

public enum
ExceptionConstants {

    SOMEERROROCCURRED("Some Error Occurred"),
    DATAALREADYEXIST("Data Already Exist"),
    CREATED("Data Created"),
    USERNOTFOUND("User Not Found"),
    DATANOTEXIST("Data doesn't Exist"),
    FORMNOTEXIST("Form Doesn't Exist"),
    USERLOCKED("User is Currently Locked");


    public final String value;
    private ExceptionConstants(String value){
        this.value=value;
    }
}
