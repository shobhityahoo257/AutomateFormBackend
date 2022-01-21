package com.formsv.AutomateForm.service.user;


import com.formsv.AutomateForm.Constants.Constants;
import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.FormRequiredDocument;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.model.supportedFields.SupportedFields;
import com.formsv.AutomateForm.model.user.User;
import com.formsv.AutomateForm.model.user.UserData;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.repository.SupportedDocRepo;
import com.formsv.AutomateForm.repository.SupportedFieldsRepo;
import com.formsv.AutomateForm.repository.form.FormRequiredDocumentRepo;
import com.formsv.AutomateForm.repository.user.UserDataRepo;
import com.formsv.AutomateForm.repository.user.UserDocumentsRepo;
import com.formsv.AutomateForm.repository.user.UserRepo;
import com.formsv.AutomateForm.responseModel.FamilyResponse;
import com.formsv.AutomateForm.responseModel.RequiredDocumentResponse;
import com.formsv.AutomateForm.responseModel.employeeResponseModel.AllUserData;
import com.formsv.AutomateForm.service.SupportedDocService;
import com.formsv.AutomateForm.service.SupportedFieldsService;
import com.formsv.AutomateForm.utils.OpenCsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.*;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;
    @Autowired
    FormRequiredDocumentRepo formRequiredDocumentRepo;
    @Autowired
    UserDocumentsRepo userDocumentsRepo;
    @Autowired
    UserDataRepo userDataRepo;
    @Autowired
    SupportedDocRepo supportedDocRepo;
    @Autowired
    SupportedFieldsService supportedFieldsService;



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
            try {
                return new ResponseEntity(userRepo.save(user), HttpStatus.CREATED);
            }catch (DuplicateKeyException e)
            {
                return new ResponseEntity("Please choose Another UserName", HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity("No User Exist", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity getRequiredDocument(String userId,String formId){
        List<FormRequiredDocument> reqdoc=formRequiredDocumentRepo.findByFormId(formId);
        List<UserDocuments> userDoc=userDocumentsRepo.findByUserId(userId);
        List<RequiredDocumentResponse.Document> list=new ArrayList<>();
        Set<String>  set=new HashSet<>();
        List<String> ids=new ArrayList<>();
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
          list.add(docu);
            ids.add(f.getDocumentId());
        }

        List<SupportedDoc> supportedDocs=supportedDocRepo.findAllBy_idIsIn(ids);

        Map<String ,String > m=new HashMap<>();
        for (SupportedDoc s:supportedDocs) {
            m.put(s.get_id(),s.getDocName());
        }

        for (int i=0;i<list.size();i++){
            list.get(i).setDocumentName(m.get(list.get(i).getDocumentId()));
        }

        RequiredDocumentResponse requiredDocumentResponse=new RequiredDocumentResponse(userId,formId,list);
         return new ResponseEntity(requiredDocumentResponse,HttpStatus.OK);
    }

    public boolean isLocked(String userId){
        if(userRepo.findUserBy_id(userId).isLock())
            return true;
        return false;

    }


    public ResponseEntity getAllUser() throws Exception{
        List<User> userList=userRepo.findAll();
        for (User user:userList) {
            user.setProfileImage(null);
        }
        AllUserData allUserData=new AllUserData();
        allUserData.setData(userList);
        return new ResponseEntity(allUserData
                ,HttpStatus.OK);
    }


    public ResponseEntity addUpdateUserData(List<UserData> userDataList,String userId) throws Exception{
        if(!isUserExistById(userId))
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
        return new ResponseEntity(userDataRepo.saveAll(userDataList),HttpStatus.OK);
    }

    public ResponseEntity deleteRequiredDocument(String id)
    {
        formRequiredDocumentRepo.deleteById(id);
        return new ResponseEntity(Constants.DELETED,HttpStatus.OK);
    }

    public boolean deleteAllRequiredDocumentsOfFOrm(String formId){
        formRequiredDocumentRepo.deleteAllByFormId(formId);
        return true;
    }


    public ResponseEntity  storeUserData(String userId,MultipartFile f) throws Exception{
             Map<String,List<String>> map=new HashMap<>();
             map=OpenCsvUtil.parseCsvFile(userId,f.getInputStream());
             return uploadUserdata(userId,map.get("field"),map.get("fieldValue"));
    }



    public ResponseEntity uploadUserdata(String userId,List<String> fieldName,List<String> fieldValue) throws Exception {
        List<UserData> userDataList = new ArrayList<>();

        try {
            if (isFieldsExist(fieldName)) {
                for (int i = 0; i < fieldValue.size(); i++) {
//                map.put(fieldName.get(i), fieldValue.get(i));
                    userDataList.add(new UserData(userId, fieldName.get(i), fieldValue.get(i)));
                }
                deleteUserData(userId, fieldName);
                return createUserdata(userDataList);
            }
        }catch (RuntimeException e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
         return null;
    }


    public boolean isFieldsExist(List<String > fieldsName){
        List<SupportedFields> f= supportedFieldsService.findAllByFieldNameIsIn(fieldsName);

        List<String> notSupported=new ArrayList<>();
        if(f.size()==fieldsName.size())
            return true;
        else{
        Set<String> s=new HashSet<>();
        for (SupportedFields supp:f) {
            s.add(supp.getFieldName());
        }
        for (String field:fieldsName) {
            if(!s.contains(field)){
                notSupported.add(field);
            }
        }
        throw new RuntimeException("These fields are not Supported By the System"+notSupported.toString());
        }
    }


    public void loadFile(Writer writer,String userId) throws IOException {
        try {
            List<UserData> userDataList =  userDataRepo.findAllByUserId(userId);

            // Using ApacheCommons Csv Utils to write Customer List objects to a Writer
            OpenCsvUtil.UserDataToCsv(writer, userDataList);

            // Using Open CSV Utils to write Customer List objects to a Writer
            // OpenCsvUtil.customersToCsv(writer, customers);
        } catch(Exception e) {
            throw new RuntimeException("Fail! -> Message = " + e.getMessage());
        }
    }


    public void deleteUserData(String userId,List<String> list){
        userDataRepo.deleteAllByUserIdAndAndFieldNameIsIn(userId,list);
    }

    public ResponseEntity createUserdata(List<UserData> userDataList)
    {
       return  new ResponseEntity(userDataRepo.saveAll(userDataList),HttpStatus.CREATED);
    }


}
