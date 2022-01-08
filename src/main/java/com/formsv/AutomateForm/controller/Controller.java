package com.formsv.AutomateForm.controller;

import com.formsv.AutomateForm.model.form.user.User;
import com.formsv.AutomateForm.repository.UserRepo;
import com.formsv.AutomateForm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class Controller {

    @Autowired
    UserService userService;
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

}
