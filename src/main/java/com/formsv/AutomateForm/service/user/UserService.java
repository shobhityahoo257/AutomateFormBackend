package com.formsv.AutomateForm.service.user;


import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.FormRequiredDocument;
import com.formsv.AutomateForm.model.image.Image;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.model.supportedFields.SupportedFields;
import com.formsv.AutomateForm.model.user.User;
import com.formsv.AutomateForm.model.user.UserData;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.repository.user.UserRepo;
import com.formsv.AutomateForm.responseModel.FamilyResponse;
import com.formsv.AutomateForm.responseModel.RequiredDocumentResponse;
import com.formsv.AutomateForm.responseModel.employeeResponseModel.AllUserData;
import com.formsv.AutomateForm.service.SupportedDocService;
import com.formsv.AutomateForm.service.SupportedFieldsService;
import com.formsv.AutomateForm.service.form.FormRequiredDocumentService;
import com.formsv.AutomateForm.service.image.ImageService;
import com.formsv.AutomateForm.utils.OpenCsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.Writer;
import java.util.*;

@Service
public class UserService {


    private final UserRepo userRepo;
    private final  FormRequiredDocumentService formRequiredDocumentService;
    private final UserDocumentService userDocumentService;
    private final  UserDataService userDataService;
    private final  SupportedDocService supportedDocService;
    private final  SupportedFieldsService supportedFieldsService;
    private final  ImageService imageService;

    @Autowired
    public UserService(UserRepo userRepo, FormRequiredDocumentService formRequiredDocumentService, UserDocumentService userDocumentService, UserDataService userDataService, SupportedDocService supportedDocService, SupportedFieldsService supportedFieldsService, ImageService imageService) {
        this.userRepo = userRepo;
        this.formRequiredDocumentService = formRequiredDocumentService;
        this.userDocumentService = userDocumentService;
        this.userDataService = userDataService;
        this.supportedDocService = supportedDocService;
        this.supportedFieldsService = supportedFieldsService;
        this.imageService = imageService;
    }


    public ResponseEntity createUser(User user) throws Exception {
         if(isUserExistByMobileNumber(user.getMobileNumber()))
             return new ResponseEntity("User is already Exist with Given Mobile Number", HttpStatus.BAD_REQUEST);
        user.setParent(true);
        User u=userRepo.save(user);
        userDocumentService.addDocument(u.get_id(),supportedDocService.findDocumentId("GENERAL"),null);
            return new ResponseEntity(u, HttpStatus.CREATED);

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


    public FamilyResponse getFamily(String mobileNumber) throws Exception{
        List<User> userList=userRepo.findByMobileNumber(mobileNumber);
        FamilyResponse familyResponse =new FamilyResponse();
        familyResponse.setMobileNumber(mobileNumber);
        familyResponse.setUsers(userList);
        return familyResponse;
    }

    public ResponseEntity addNewMember(User user) throws Exception {
        try {
            return new ResponseEntity(userRepo.save(user), HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity("Please choose Another UserName", HttpStatus.BAD_REQUEST);

        }
    }

    public ResponseEntity getRequiredDocument(String userId,String formId){
        List<FormRequiredDocument> reqdoc=formRequiredDocumentService.findByFormId(formId);
        List<UserDocuments> userDoc=userDocumentService.findByUserId(userId);
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

        List<SupportedDoc> supportedDocs=supportedDocService.findAllBy_idIsIn(ids);

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
        return new ResponseEntity(userList
                ,HttpStatus.OK);
    }


    public ResponseEntity addUpdateUserData(List<UserData> userDataList,String userId) throws Exception{
        return new ResponseEntity(userDataService.saveAll(userDataList),HttpStatus.OK);
    }

    public List<UserData> getUserData(String userId, String documentId){
       List<SupportedFields> sf= supportedFieldsService.findAllByDocumentId(documentId);
       Map<String,String> map=new HashMap<>();
        for (SupportedFields s:sf) {
            map.put(s.get_id(),s.getFieldName());
        }


       List<UserData> userDataList= userDataService.findAllByUserId(userId);
       Set<String> set=new HashSet<>();
        for (UserData ud:userDataList) {
            set.add(ud.getFieldId());
            ud.setFieldName(map.get(ud.getFieldId()));
        }

        for (SupportedFields s:sf) {
           if(!set.contains(s.get_id()))
           {
               UserData ud=new UserData();
               ud.setFieldId(s.get_id());
               ud.setFieldName(s.getFieldName());
               ud.setDocumentId(s.getDocumentId());
               userDataList.add(ud);
           }
        }

      return userDataList;


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
            List<UserData> userDataList =  userDataService.findAllByUserId(userId);

            // Using ApacheCommons Csv Utils to write Customer List objects to a Writer
            OpenCsvUtil.UserDataToCsv(writer, userDataList);

            // Using Open CSV Utils to write Customer List objects to a Writer
            // OpenCsvUtil.customersToCsv(writer, customers);
        } catch(Exception e) {
            throw new RuntimeException("Fail! -> Message = " + e.getMessage());
        }
    }


    public void deleteUserData(String userId,List<String> list){
     //   userDataRepo.deleteAllByUserIdAndAndFieldNameIsIn(userId,list);
    }

    public ResponseEntity createUserdata(List<UserData> userDataList)
    {
       return  new ResponseEntity(userDataService.saveAll(userDataList),HttpStatus.CREATED);
    }

   public User updateUser(String userId,MultipartFile f,String userName) throws IOException {
        User u=userRepo.findUserBy_id(userId);
        if(f!=null) {
            Image image=new Image(u.get_id(),f.getBytes());
            imageService.saveImage(image);
        }
        else
            u.setProfileImageId(null);
        u.setUserName(userName);
        return userRepo.save(u);
   }

   public List<User> updateMobileNumber(String userId,String mobileNumber){
        User u=userRepo.findUserBy_id(userId);
        List<User> userList=userRepo.findByMobileNumber(u.getMobileNumber());
      for(int i=0;i<userList.size();i++){
          userList.get(i).setMobileNumber(mobileNumber);
      }
        return userRepo.saveAll(userList);
   }

   //userId is treated as userName & userName is treated as pass

   public org.springframework.security.core.userdetails.User loadUserByUsername(String userId){
       User user=userRepo.findUserBy_id(userId);
       return new org.springframework.security.core.userdetails.User(user.get_id(), user.getUserName(),new ArrayList<>());
   }

   private void createUserDetails(String userId,String userName){

   }

   public User setLock(String userId) throws Exception{
        User u=userRepo.findUserBy_id(userId);
        u.setLock(true);
        return userRepo.save(u);
   }

   public User releaseLock(String userid) throws Exception{
           User u = userRepo.findUserBy_id(userid);
           u.setLock(false);
         return   userRepo.save(u);
   }

   public boolean isUserLocked(String userid) throws NullPointerException{
      return userRepo.findUserBy_id(userid).isLock();
   }

   public boolean isFamilyExist(String mobileNumber){
       List<User> u=userRepo.findByMobileNumber(mobileNumber);
       if( u==null|| u.size()==0)
           return false;
       return true;
   }

   public User findById(String userId){
        return userRepo.findUserBy_id(userId);
   }
}


