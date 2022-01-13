package com.formsv.AutomateForm.responseModel.employeeResponseModel;

import com.formsv.AutomateForm.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllUserData {
    private List<User> data;
}
