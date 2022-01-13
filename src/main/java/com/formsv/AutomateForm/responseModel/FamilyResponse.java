package com.formsv.AutomateForm.responseModel;


import com.formsv.AutomateForm.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class FamilyResponse {

     private String mobileNumber;
     private List<User> users;

}
