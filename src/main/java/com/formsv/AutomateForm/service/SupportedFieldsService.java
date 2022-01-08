package com.formsv.AutomateForm.service;

import com.formsv.AutomateForm.Constants;
import com.formsv.AutomateForm.model.form.user.SupportedFields;
import com.formsv.AutomateForm.repository.SupportedFieldsRepo;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SupportedFieldsService {
    @Autowired
    SupportedFieldsRepo supportedFieldsRepo;

    public ResponseEntity createSupportedFields(List<SupportedFields> list) {
        try {
            List<SupportedFields> l = supportedFieldsRepo.insert(list);
            return new ResponseEntity(l, HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity(Constants.DATAALREADYEXIST.value, HttpStatus.BAD_REQUEST);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            return new ResponseEntity(Constants.DATAALREADYEXIST.value, HttpStatus.BAD_REQUEST);
        }
    }



}
