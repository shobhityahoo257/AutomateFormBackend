package com.formsv.AutomateForm.controller;

import com.formsv.AutomateForm.model.form.user.SupportedDoc;
import com.formsv.AutomateForm.model.form.user.SupportedFields;
import com.formsv.AutomateForm.model.form.user.User;
import com.formsv.AutomateForm.repository.SupportedDocRepo;
import com.formsv.AutomateForm.repository.SupportedFieldsRepo;
import com.formsv.AutomateForm.repository.UserRepo;
import com.formsv.AutomateForm.service.SupportedDocService;
import com.formsv.AutomateForm.service.SupportedFieldsService;
import com.formsv.AutomateForm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class Controller {

    @Autowired
    UserService userService;
    @Autowired
    SupportedFieldsService supportedFieldsService;
    @Autowired
    SupportedDocService supportedDocService;
//In case of parent call we will have username as null
    @PostMapping("/createUser")
    public ResponseEntity createUser(@RequestBody(required = true) User user)
    {
        try {
            return userService.createUser(user);
        }catch (Exception e){
            return  new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createSupportedFields")
    public ResponseEntity createSupportedFields(@RequestBody(required = true) List<SupportedFields> list){
       try {
          return supportedFieldsService.createSupportedFields(list);
       }catch (Exception e)
       {
           return  new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @PostMapping("/createSupportedDoc")
    public ResponseEntity createSupportedDoc(@RequestBody(required = true) List<SupportedDoc> list){
        try {
            return supportedDocService.createSupportedDoc(list);
        }catch (Exception e)
        {
            return  new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
