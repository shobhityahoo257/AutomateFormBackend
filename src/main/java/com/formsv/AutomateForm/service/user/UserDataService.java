package com.formsv.AutomateForm.service.user;


import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.user.MultipleUserData;
import com.formsv.AutomateForm.model.user.UserData;
import com.formsv.AutomateForm.repository.user.UserDataRepo;
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
                return new ResponseEntity(ExceptionConstants.DATAALREADYEXIST.value, HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity(ExceptionConstants.USERNOTFOUND, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity updateUserData(UserData userData){
        UserData d= userDataRepo.findByUserIdAndFieldId(userData.getUserId(),userData.getFieldId());
        if(d==null)
            return new ResponseEntity(ExceptionConstants.DATANOTEXIST, HttpStatus.BAD_REQUEST);
        d.setFieldValue(userData.getFieldValue());
        return new ResponseEntity(userDataRepo.save(d),HttpStatus.OK);
    }


    public void getUserAllData(String id){
           userDataRepo.findAllByUserId(id);
    }

}