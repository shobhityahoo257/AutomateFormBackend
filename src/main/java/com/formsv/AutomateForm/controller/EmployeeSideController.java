package com.formsv.AutomateForm.controller;

import com.formsv.AutomateForm.Constants.Constants;
import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.Form;
import com.formsv.AutomateForm.model.form.FormIdsPojo;
import com.formsv.AutomateForm.model.form.FormRequiredDocument;
import com.formsv.AutomateForm.model.requestModel.SupportedDocRequest;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.model.supportedFields.SupportedFields;
import com.formsv.AutomateForm.model.user.MultipleUserData;
import com.formsv.AutomateForm.model.user.UserData;
import com.formsv.AutomateForm.service.*;
import com.formsv.AutomateForm.service.form.AppliedFormService;
import com.formsv.AutomateForm.service.form.FormService;
import com.formsv.AutomateForm.service.image.ImageService;
import com.formsv.AutomateForm.service.user.UserDataService;
import com.formsv.AutomateForm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController()
public class EmployeeSideController {

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


     @GetMapping("/getAllUsers")
    public ResponseEntity getAllUser() throws Exception {
           return userService.getAllUser();
     }

     @GetMapping("/getUserData/user/{userId}/document/{documentId}")
     public ResponseEntity getUserData(@PathVariable("userId") String userid,@PathVariable("documentId") String documentId) throws Exception {
             if(!userService.isUserExistById(userid))
                 return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
             if(!supportedDocService.isDocumentExistById(documentId))
                 return new ResponseEntity(ExceptionConstants.NODOCUMENT.value,HttpStatus.BAD_REQUEST);
        return new ResponseEntity(userService.getUserData(userid,documentId),HttpStatus.OK);
     }


    /**
     *  this is used to to add or update existing userData
     * @param
     * @param userid
     * @return
     * @throws Exception
     */
    @PostMapping("/storeUserData/{userId}")
    public ResponseEntity storeUserData(@RequestBody MultipleUserData multipleUserData, @PathVariable("userId") String userid) throws Exception {
        if(!userService.isUserExistById(userid))
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
        if(!supportedDocService.isDocumentExistById(multipleUserData.getDocumentId()))
            return new ResponseEntity(ExceptionConstants.NODOCUMENT.value,HttpStatus.BAD_REQUEST);
        for (UserData ud: multipleUserData.getUserDataList()) {
            ud.setUserId(userid);
            ud.setDocumentId(multipleUserData.getDocumentId());
        }
        //Code to check iF fields are supported by the Document

        return userService.addUpdateUserData(multipleUserData.getUserDataList(),userid);
    }

    @DeleteMapping("/deleteSingleUserField/{userId}/{fieldId}")
    public ResponseEntity deleteSingleUserField(@PathVariable("userId") String userid,@PathVariable("fieldId") String fieldId) throws Exception {
        if(!userService.isUserExistById(userid))
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
       userDataService.deleteSingleUserField(fieldId);
        return new ResponseEntity(Constants.DELETED,HttpStatus.OK);
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
        if(! formService.isFormExist(formId))
            return new ResponseEntity(ExceptionConstants.FORMNOTEXIST,HttpStatus.BAD_REQUEST);
        f.setModifiedAt(new Date());
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
        if(! formService.isFormExist(formId))
            return new ResponseEntity(ExceptionConstants.FORMNOTEXIST,HttpStatus.BAD_REQUEST);
        return formService.deleteForm(formId);
    }

    @PostMapping("/addRequiredDocuments/{formId}")
    public ResponseEntity addRequiredDocuments(@PathVariable("formId") String formId,@RequestBody List<FormRequiredDocument> rdoc) throws Exception {
        return supportedDocService.addSupportedDocumentforForm(formId,rdoc);
    }

    @DeleteMapping("/deleteRequiredDocuments/{formId}")
    public ResponseEntity deleteRequiredDocument(@PathVariable("formId") String formId,@RequestBody List<FormRequiredDocument> rdoc) throws Exception {
        if(! formService.isFormExist(formId))
            return new ResponseEntity(ExceptionConstants.FORMNOTEXIST,HttpStatus.BAD_REQUEST);
        try {
            supportedDocService.deleteRequiredDocument(formId, rdoc);
        }catch (Exception e)
        {
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(Constants.DELETED,HttpStatus.OK);
    }




    @PostMapping("/createSupportedDoc")
    public ResponseEntity createUpdateSupportedDoc(@RequestBody SupportedDocRequest supportedDocRequest) throws Exception {
        SupportedDoc supportedDoc=new SupportedDoc(supportedDocRequest.get_id(),supportedDocRequest.getDocName());
       String docId= supportedDocService.createSupportedDoc(supportedDoc);
        if(supportedDocRequest.get_id()!=null)
        supportedFieldsService.deleteAllByDocumentId(supportedDocRequest.get_id());
       return supportedFieldsService.createSupportedFields(supportedDocRequest.getFieldsList(),docId);
    }

//    @PutMapping("/updateSupportedDoc/{documentId}")
//    public ResponseEntity updateSupportedDoc(@PathVariable("documentId") String documentId,@RequestBody SupportedDoc doc){
//        if(!supportedDocService.isDocumentExistById(documentId))
//            return new ResponseEntity(ExceptionConstants.NODOCUMENT,HttpStatus.BAD_REQUEST);
//        return new ResponseEntity(supportedDocService.updateSupportedDoc(doc),HttpStatus.BAD_REQUEST);
//    }

    @GetMapping("/getAllSupportedDoc")
    public ResponseEntity getAllSupportedDoc(){
        return new ResponseEntity(supportedDocService.findAll(),HttpStatus.OK);
    }

    @GetMapping("/getSupportedFields/{documentId}")
    public ResponseEntity getSupportedFields(@PathVariable("documentId") String documentId){
       return new ResponseEntity(supportedFieldsService.findAllByDocumentId(documentId),HttpStatus.OK);
    }


//
//    @PostMapping("/addUserData/{userId}")
//    public ResponseEntity addUserData(@PathVariable("userId") String userId,
//                                       @RequestParam("document") MultipartFile document)      throws Exception {
//
//        return userService.storeUserData(userId,document);
//
//    }

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