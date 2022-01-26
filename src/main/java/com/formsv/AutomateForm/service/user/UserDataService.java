package com.formsv.AutomateForm.service.user;


import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.user.MultipleUserData;
import com.formsv.AutomateForm.model.user.User;
import com.formsv.AutomateForm.model.user.UserData;
import com.formsv.AutomateForm.repository.user.UserDataRepo;
import com.formsv.AutomateForm.service.image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserDataService {

    private final UserDataRepo userDataRepo;

    @Autowired
    public UserDataService(UserDataRepo userDataRepo) {
        this.userDataRepo = userDataRepo;
    }


//    public ResponseEntity createMultipleUserdata(MultipleUserData userData) {
//        if (userService.isUserExistById(userData.getUserId())) {
//            //Validate that if fieldValue is valid or Not
//
//            List<UserData> list = new ArrayList<>();
//            for (UserData d : userData.getUserDataList()) {
//                d.setUserId(userData.getUserId());
//                list.add(d);
//            }
//            try {
//                userDataRepo.saveAll(list);
//            } catch (org.springframework.dao.DuplicateKeyException e) {
//                return new ResponseEntity(ExceptionConstants.DATAALREADYEXIST.value, HttpStatus.BAD_REQUEST);
//            }
//        }
//        return new ResponseEntity(ExceptionConstants.USERNOTFOUND, HttpStatus.NOT_FOUND);
//    }

   public List<UserData> saveAll(List<UserData> list){
        return userDataRepo.saveAll(list);
   }
    public void deleteSingleUserField(String fieldId) throws Exception{
        userDataRepo.deleteById(fieldId);
    }

    public List<UserData>  findAllByUserId(String userId){
        return userDataRepo.findAllByUserId(userId);
    }




}
