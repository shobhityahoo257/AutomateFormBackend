package com.formsv.AutomateForm.service;


import com.formsv.AutomateForm.model.form.user.User;
import com.formsv.AutomateForm.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    public ResponseEntity createUser(User user) {
        //Parent
        if (user.getUserName() == null) {
            if (isParentExist(user))
                return new ResponseEntity("Already Exist",HttpStatus.BAD_REQUEST);
            else {
                user.setNoOfMembers(1L);
                return save(user);
            }
        }
        //Create Call for child
        else{
               if(isChildExist(user))
                   return new ResponseEntity("Child Already Exist Choose Another Name",HttpStatus.BAD_REQUEST);
               else {
                   if (addChild(user))
                   {
                   user.setNoOfMembers(null);
                   return save(user);
                   }
                   else return new ResponseEntity("Some Error Occurred",HttpStatus.INTERNAL_SERVER_ERROR);
               }
        }

    }


    public boolean isParentExist(User user)
    {
       if( userRepo.findUserByMobileNumberAndNoOfMembersIsNotNull(user.getMobileNumber())==null)
           return false;
       return true;
    }

    public boolean isChildExist(User user){
        if( userRepo.findUserByMobileNumberAndUserName(user.getMobileNumber(),user.getUserName())==null)
            return false;
        return true;
    }

    public ResponseEntity save(User user){
        String id=userRepo.save(user).get_id();
        if(id==null)
           new ResponseEntity("SomeError Occurred",HttpStatus.INTERNAL_SERVER_ERROR);
        else
             new ResponseEntity(id,HttpStatus.CREATED);
        return new ResponseEntity("SomeError Occurred",HttpStatus.INTERNAL_SERVER_ERROR);

    }

    public boolean addChild(User user){
       User parent = userRepo.findUserByMobileNumberAndNoOfMembersIsNotNull(user.getMobileNumber());
       parent.setNoOfMembers(parent.getNoOfMembers()+1);
       String id=userRepo.save(parent).get_id();
       if(id==null)
           return false;
       else
           return true;
    }

    public boolean isUserExistById(String id){
         if(userRepo.findUserBy_id(id)==null)
             return false;
         return true;
    }


}
