package com.formsv.AutomateForm.service.user;


import com.formsv.AutomateForm.model.form.FormRequiredDocument;
import com.formsv.AutomateForm.model.user.User;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.repository.form.FormRequiredDocumentRepo;
import com.formsv.AutomateForm.repository.user.UserDocumentsRepo;
import com.formsv.AutomateForm.repository.user.UserRepo;
import com.formsv.AutomateForm.responseModel.FamilyResponse;
import com.formsv.AutomateForm.responseModel.RequiredDocumentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;
    @Autowired
    FormRequiredDocumentRepo formRequiredDocumentRepo;
    @Autowired
    UserDocumentsRepo userDocumentsRepo;

    public ResponseEntity createUser(User user) throws Exception {
        user.setParent(true);
        return  new ResponseEntity(userRepo.save(user),HttpStatus.CREATED);
    }


    public boolean isParentExist(User user)
    {
    //   if( userRepo.findUserByMobileNumberAndNoOfMembersIsNotNull(user.getMobileNumber())==null)
     //      return false;
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
//          User parent = userRepo.findUserByMobileNumberAndNoOfMembersIsNotNull(user.getMobileNumber());
//      // parent.setNoOfMembers(parent.getNoOfMembers()+1);
//       String id=userRepo.save(parent).get_id();
//       if(id==null)
//           return false;
//       else
          return true;
    }

    public boolean isUserExistById(String id){
         if(userRepo.findUserBy_id(id)==null)
             return false;
         return true;
    }

    public boolean isUserExistByMobileNumber(String mobileNumber) throws Exception{
       List<User> userList= userRepo.findByMobileNumber(mobileNumber);
         if(userList==null || userList.size()==0)
             return false;
         return true;
    }


    public ResponseEntity getFamily(String mobileNumber) throws Exception{
        List<User> userList=userRepo.findByMobileNumber(mobileNumber);
        FamilyResponse familyResponse =new FamilyResponse();
        if(userList==null || userList.size()==0)
            return new ResponseEntity("NO Family Exist",HttpStatus.NOT_FOUND);
        else
        {

            familyResponse.setMobileNumber(mobileNumber);
            familyResponse.setUsers(userList);
        }
        return new ResponseEntity(familyResponse,HttpStatus.OK);
    }

    public ResponseEntity addNewMember(User user) throws Exception {
        if(isUserExistByMobileNumber(user.getMobileNumber()))
        {
            return new ResponseEntity(userRepo.save(user),HttpStatus.CREATED);
        }
        else
            return new ResponseEntity("No User Exist",HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity getRequiredDocument(String userId,String formId){
        List<FormRequiredDocument> reqdoc=formRequiredDocumentRepo.findByFormId(formId);
        List<UserDocuments> doc=userDocumentsRepo.findByUserId(userId);
        RequiredDocumentResponse requiredDocumentResponse=new RequiredDocumentResponse();
        Set<String>  set=new HashSet<>();
        for (UserDocuments document:doc) {
                    set.add(document.getDocumentId());
        }
        for (FormRequiredDocument f:reqdoc) {
            RequiredDocumentResponse.Document docu=new RequiredDocumentResponse.Document();
            docu.setDocumentId(f.getDocumentId());
            if(set.contains(f.getDocumentId())){
                docu.setUploadedByUser(true);
                //docu.setDocumentName();
            }
            requiredDocumentResponse.getDoc().add(docu);
        }

         return new ResponseEntity(requiredDocumentResponse,HttpStatus.OK);
    }

}
