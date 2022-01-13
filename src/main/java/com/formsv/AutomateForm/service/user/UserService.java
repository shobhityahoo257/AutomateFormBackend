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
import org.springframework.dao.DuplicateKeyException;
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
         if(isUserExistByMobileNumber(user.getMobileNumber()))
             return new ResponseEntity("User is already Exist with Given Mobile Number", HttpStatus.BAD_REQUEST);
        user.setParent(true);
            return new ResponseEntity(userRepo.save(user), HttpStatus.CREATED);

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
        List<UserDocuments> userDoc=userDocumentsRepo.findByUserId(userId);
        RequiredDocumentResponse requiredDocumentResponse=new RequiredDocumentResponse();
        Set<String>  set=new HashSet<>();
        for (UserDocuments document:userDoc) {
                    set.add(document.getDocumentId());
        }
        for (FormRequiredDocument f:reqdoc) {
            RequiredDocumentResponse.Document docu=new RequiredDocumentResponse.Document();
            docu.setDocumentId(f.getDocumentId());
            docu.setDocumentName(f.getDocumentName());
            if(set.contains(f.getDocumentId())){
                docu.setUploadedByUser(true);
            }
            requiredDocumentResponse.getDoc().add(docu);
        }

         return new ResponseEntity(requiredDocumentResponse,HttpStatus.OK);
    }

}
