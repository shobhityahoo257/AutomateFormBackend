package com.formsv.AutomateForm.responseModel;

import com.formsv.AutomateForm.model.form.Form;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserFormResponse {
    private String userId;
    private String mobileNumber;
    private String userName;
    private List<Form> formList;
}
