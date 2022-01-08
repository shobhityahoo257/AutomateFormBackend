package com.formsv.AutomateForm.service;


import com.formsv.AutomateForm.Constants;
import com.formsv.AutomateForm.model.form.user.MultipleUserData;
import com.formsv.AutomateForm.model.form.user.UserData;
import com.formsv.AutomateForm.repository.UserDataRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDataService {
    @Autowired
    UserDataRepo userDataRepo;
    @Autowired
    UserService userService;

    public ResponseEntity createMultipleUserdata(MultipleUserData userData) {
        if (userService.isUserExistById(userData.getUserId())) {
            //Validate that if fieldValue is valid or Not
            List<UserData> list = new ArrayList<>();
            for (UserData d : userData.getUserDataList()) {
                d.setUserId(userData.getUserId());
                list.add(d);
            }
            try {
                userDataRepo.saveAll(list);
            } catch (org.springframework.dao.DuplicateKeyException e) {
                return new ResponseEntity(Constants.DATAALREADYEXIST.value, HttpStatus.BAD_REQUEST);
            }

        }
        return new ResponseEntity(Constants.USERNOTFOUND, HttpStatus.NOT_FOUND);
    }
}
