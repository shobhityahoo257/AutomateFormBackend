package com.formsv.AutomateForm.controller;

import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.AppliedForm;
import com.formsv.AutomateForm.model.form.Form;
import com.formsv.AutomateForm.model.form.FormIdsPojo;
import com.formsv.AutomateForm.model.form.Transaction;
import com.formsv.AutomateForm.model.image.Image;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.model.supportedFields.SupportedFields;
import com.formsv.AutomateForm.model.user.User;
import com.formsv.AutomateForm.model.user.UserData;
import com.formsv.AutomateForm.service.*;
import com.formsv.AutomateForm.service.form.AppliedFormService;
import com.formsv.AutomateForm.service.form.FormService;
import com.formsv.AutomateForm.service.form.TransactionsService;
import com.formsv.AutomateForm.service.image.ImageService;
import com.formsv.AutomateForm.service.user.UserDataService;
import com.formsv.AutomateForm.service.user.UserService;
import com.formsv.AutomateForm.utils.OpenCsvUtil;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CrossOrigin(origins = "*")
@RestController()
public class Controller {

    @Autowired
    UserService userService;
    @Autowired
    SupportedFieldsService supportedFieldsService;
    @Autowired
    SupportedDocService supportedDocService;
    @Autowired
    UserDataService userDataService;
    @Autowired
    AppliedFormService appliedFormService;
    @Autowired
    ImageService imageService;
    @Autowired
    FormService formService;
    @Autowired
    TransactionsService transactionsService;


    @GetMapping("/")
    public String hello() {
        return "hello world!";
    }


    @GetMapping("getFamily/{mobileNumber}")
    public ResponseEntity getFamily(@PathVariable("mobileNumber") String mobileNumber) throws Exception {
        return userService.getFamily(mobileNumber);
    }

    @PostMapping("/createUser")
    public ResponseEntity createUser(@RequestParam(value = "userName",required = false) String userName,@RequestParam("mobileNumber") String mobileNumber,@RequestParam(value = "profileImage", required = false) MultipartFile profileImage ) throws Exception {
        User user=new User();
        user.setParent(true);
        if(userName!=null)
        user.setUserName(userName);
        user.setMobileNumber(mobileNumber);
       // user.setProfileImage(new Binary(BsonBinarySubType.BINARY, profileImage.getBytes()));
        if(profileImage!=null)
        user.setProfileImage(profileImage.getBytes());
        user.setCreatedAt(new Date());
        user.setModifiedAt(user.getCreatedAt());
        return userService.createUser(user);
    }

    @PostMapping("/addNewMember/{userName}/{mobileNumber}")
    public ResponseEntity addMember(@PathVariable("userName") String userName,@PathVariable("mobileNumber") String mobileNumber,@RequestParam("profileImage") MultipartFile profileImage ) throws Exception {
        User user=new User();
        user.setUserName(userName);
        user.setMobileNumber(mobileNumber);
        //user.setProfileImage(new Binary(BsonBinarySubType.BINARY, profileImage.getBytes()));
         user.setProfileImage(profileImage.getBytes());
        return userService.addNewMember(user);
    }


    @GetMapping("/getAllForms/{userId}")
    public ResponseEntity getAllFormsOfUser(@PathVariable("userId") String useId) throws Exception {
        return formService.getAllFormsOfUser(useId);
    }
/*
This is used to add Required Documents for a form
 */






//    @PostMapping("/createUserData")
//    public ResponseEntity createMultipleUserData(@RequestBody MultipleUserData userData) {
//        try {
//            return userDataService.createMultipleUserdata(userData);
//        } catch (Exception e) {
//            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


    @PostMapping("/submitForm/{userId}/{formId}")
    public ResponseEntity applyForm(@PathVariable("formId") String formid,@PathVariable("userId") String userId) throws Exception {
        AppliedForm appliedForm = new AppliedForm();
        appliedForm.setFormId(formid);
        appliedForm.setUserId(userId);
        appliedForm.setStatus(AppliedForm.Status.valueOf("PENDING"));
        return appliedFormService.create(appliedForm);
    }

    @PostMapping("/submitTransaction")
    public ResponseEntity submitTransaction(@RequestBody(required = true) Transaction transaction) throws Exception {
        System.out.println("WITHIN SUBMIT TRANSACTION");
        System.out.println("TRANSACTION STATUS IS "+ transaction.getTransactionStatus());
        if(transaction.getTransactionStatus().equals("SUCCESS")) {
           // add Retry Framework Here
            applyForm(transaction.getFormId(), transaction.getUserId());
        }
        System.out.println("FULL TRANSATION IS"+transaction.toString());
       return transactionsService.createTransaction(transaction);
    }




    /*
      This is used to upload single document of user
     */

    @PostMapping("/uploadSingleImage/{userId}/{documentId}")
    public ResponseEntity addDocuments(@PathVariable("userId") String userId,
                           @RequestParam("document") MultipartFile document,@PathVariable("documentId") String documentId) throws Exception {
        return imageService.addPhoto(userId,documentId,document);
    }



    @GetMapping("/getRequiredDocument/{userId}/{formId}")
    public ResponseEntity getRequiredDocument(@PathVariable("userId") String userId,@PathVariable("formId") String formId){
        return userService.getRequiredDocument(userId,formId);
    }

    @GetMapping("/getUserSingleDocument/{userId}/{documentId}")
    public ResponseEntity getPhoto(@PathVariable("userId") String userId,@PathVariable("documentId") String documentId) {
        if(!userService.isUserExistById(userId))
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
        return new ResponseEntity(userDataService.getUserDocuments(userId,documentId),HttpStatus.OK);
    }



    @GetMapping("/getAllSupportedDocuments/{userId}")
    public ResponseEntity getAllSupportedDocuments(@PathVariable("userId") String userId){
        return new ResponseEntity(supportedDocService.getAllSupportedDocuments(userId),HttpStatus.OK);
    }


    @GetMapping("/getAllDocumentsOfUser/{userId}")
    public  ResponseEntity getAllDocumentsOfUser(@PathVariable("userId") String userId) throws IOException {
        if(!userService.isUserExistById(userId))
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
        return new ResponseEntity(userDataService.getAllUserDocuments(userId), HttpStatus.OK);
    }
















    //Employee side






     @GetMapping("/getAllUsers")
    public ResponseEntity getAllUser() throws Exception {
         System.out.println("In getAllUsers");
           return userService.getAllUser();
     }



    /**
     *  this is used to to add or update existing userData
     * @param userDataList
     * @param userid
     * @return
     * @throws Exception
     */
    @PostMapping("/storeUserData/{userid}")
    public ResponseEntity storeUserData(@RequestBody List<UserData> userDataList,@PathVariable("userId") String userid) throws Exception {
        return userService.addUpdateUserData(userDataList,userid);
    }

    /*
    It will Be used by employee
     */
    @PostMapping("/completeForm")
    public ResponseEntity completeForm(@RequestParam String userId,String formId){
        try {
            return  appliedFormService.completeForm(userId, formId);
        }catch (Exception e){
            return new ResponseEntity(ExceptionConstants.SOMEERROROCCURRED,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @PutMapping("/updateUserData")
//    public ResponseEntity updateUserData(@RequestBody UserData userData) {
//        try {
//            return userDataService.updateUserData(userData);
//        } catch (Exception e) {
//            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PostMapping("/createSupportedFields")
    public ResponseEntity createSupportedFields(@RequestBody(required = true) List<SupportedFields> list) {
        try {
            return supportedFieldsService.createSupportedFields(list);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createSupportedDoc")
    public ResponseEntity createSupportedDoc(@RequestBody(required = true) List<SupportedDoc> list) {
        try {
            return supportedDocService.createSupportedDoc(list);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     * CreateForm
     * @param f
     * @return
     * @throws Exception
     */


    @PostMapping("/createForm")
    public ResponseEntity createForm(@RequestBody Form f) throws Exception {
        f.setCreatedAt(new Date());
        f.setModifiedAt(f.getCreatedAt());
        return formService.createForm(f);
    }

    @GetMapping("/getAllForms")
    public ResponseEntity getAllForms()throws Exception{
        return formService.getAllForms();
    }


    /**
     * update Form Data
     * @param f
     * @param formId
     * @return
     * @throws Exception
     */

    @PutMapping("/updateForm/{formId}")
    public ResponseEntity updateForm(@RequestBody Form f,@PathVariable("formId") String formId) throws Exception {
        f.setModifiedAt(new Date());
        if(f.get_id()==null)
            f.set_id(formId);
        return formService.updateFormData(formId,f);
    }

    /**
     * deleteForm
     * @param formId
     * @return
     */

    @DeleteMapping("/deleteForm/{formId}")
    public ResponseEntity deleteForm(@PathVariable("formId") String formId) throws Exception {
        return formService.deleteForm(formId);
    }

    @PostMapping("/addRequiredDocuments/{formId}")
    public ResponseEntity addRequiredDocuments(@PathVariable("formId") String formId,@RequestBody FormIdsPojo rdoc) throws Exception {
        return supportedDocService.addSupportedDocumentforForm(formId,rdoc);
    }


    @PostMapping("/addUserData/{userId}")
    public ResponseEntity addUserData(@PathVariable("userId") String userId,
                                       @RequestParam("document") MultipartFile document)      throws Exception {

        return userService.storeUserData(userId,document);

    }

    @GetMapping("/api/download/csv/{userId}")
    public void downloadFile(HttpServletResponse response,@PathVariable("userId") String userId ) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=userData_" + userId + ".csv";
        response.setHeader(headerKey,headerValue);
        userService.loadFile(response.getWriter(),userId);
    }




}