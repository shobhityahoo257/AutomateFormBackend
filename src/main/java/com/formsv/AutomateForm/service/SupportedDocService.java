package com.formsv.AutomateForm.service;

import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.repository.SupportedDocRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupportedDocService {
    @Autowired
    SupportedDocRepo supportedDocRepo;

    public ResponseEntity createSupportedDoc(List<SupportedDoc> list) {
        try {
            List<SupportedDoc> l = supportedDocRepo.insert(list);
            return new ResponseEntity(l, HttpStatus.CREATED);
        }
        catch (org.springframework.dao.DuplicateKeyException e) {
            return new ResponseEntity(ExceptionConstants.DATAALREADYEXIST.value, HttpStatus.BAD_REQUEST);
        }
    }
}
