package com.formsv.AutomateForm.controller;

import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.AppliedForm;
import com.formsv.AutomateForm.model.form.Form;
import com.formsv.AutomateForm.model.image.Image;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.model.supportedFields.SupportedFields;
import com.formsv.AutomateForm.model.user.MultipleUserData;
import com.formsv.AutomateForm.model.user.User;
import com.formsv.AutomateForm.model.user.UserData;
import com.formsv.AutomateForm.service.*;
import com.formsv.AutomateForm.service.form.AppliedFormService;
import com.formsv.AutomateForm.service.form.FormService;
import com.formsv.AutomateForm.service.image.ImageService;
import com.formsv.AutomateForm.service.user.UserDataService;
import com.formsv.AutomateForm.service.user.UserService;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@RestController
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


    @GetMapping("/")
    public String hello() {
        return "hello world!";
    }


    @GetMapping("getFamily/{mobileNumber}")
    public ResponseEntity getFamily(@PathVariable("mobileNumber") String mobileNumber) throws Exception {
        return userService.getFamily(mobileNumber);
    }

    @PostMapping("/createUser/{userName}/{mobileNumber}")
    public ResponseEntity createUser(@PathVariable("userName") String userName,@PathVariable("mobileNumber") String mobileNumber,@RequestParam("profileImage") MultipartFile profileImage ) throws Exception {
        User user=new User();
        user.setParent(true);
        user.setUserName(userName);
        user.setMobileNumber(mobileNumber);
        user.setProfileImage(new Binary(BsonBinarySubType.BINARY, profileImage.getBytes()));
        return userService.createUser(user);
    }

    @PostMapping("/addNewMember/{userName}/{mobileNumber}")
    public ResponseEntity addMember(@PathVariable("userName") String userName,@PathVariable("mobileNumber") String mobileNumber,@RequestParam("profileImage") MultipartFile profileImage ) throws Exception {
        User user=new User();
        user.setUserName(userName);
        user.setMobileNumber(mobileNumber);
        user.setProfileImage(new Binary(BsonBinarySubType.BINARY, profileImage.getBytes()));
        return userService.addNewMember(user);
    }

    @PostMapping("/createForm")
    public ResponseEntity createForm(@RequestBody Form f) throws Exception {
        f.setCreatedAt(new Date());
        f.setModifiedAt(new Date());
        return formService.createForm(f);
    }

    @GetMapping("/getAllForms/{userId}")
    public ResponseEntity getAllFormsOfUser(@PathVariable("userId") String useId) throws Exception {
        return formService.getAllFormsOfUser(useId);
    }





    @GetMapping("/getRequiredDocuments/{userId}/{formId}")
    public ResponseEntity getRequiredDocumentsOfUserForParticularForm(@PathVariable ("userId") String userId,@PathVariable ("formId") String formId )
    {

        return null;
    }











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

//    @PostMapping("/createUserData")
//    public ResponseEntity createMultipleUserData(@RequestBody MultipleUserData userData) {
//        try {
//            return userDataService.createMultipleUserdata(userData);
//        } catch (Exception e) {
//            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PutMapping("/updateUserData")
    public ResponseEntity updateUserData(@RequestBody UserData userData) {
        try {
            return userDataService.updateUserData(userData);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/submitForm/{userId}/{formId}")
    public ResponseEntity applyForm(@PathVariable("formId") String formid,@PathVariable("userId") String userId) {
        AppliedForm appliedForm=new AppliedForm();
        appliedForm.setFormId(formid);
        appliedForm.setUserId(userId);
        try {
            return appliedFormService.create(appliedForm);
        }
        catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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


    @PostMapping("/photos/add")
    public ResponseEntity addPhoto(@PathVariable String userId,
                           @RequestParam("image") MultipartFile image,@PathVariable String documentId)
            throws Exception {
        String id = imageService.addPhoto(userId,documentId,image);
        return new ResponseEntity(id,HttpStatus.CREATED);
    }


    @GetMapping("/photos/{id}")
    public ResponseEntity getPhoto(@PathVariable String id) {
        Image photo = imageService.getPhoto(id);
//        model.addAttribute("title", photo.getTitle());
//        model.addAttribute("image",
//                Base64.getEncoder().encodeToString(photo.getImage().getData()));
       // return new ResponseEntity(,HttpStatus.OK);// new ResponseEntity;
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(photo.getImage().getData());
    }







}
