package com.formsv.AutomateForm.service.form;

import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.AppliedForm;
import com.formsv.AutomateForm.repository.form.AppliedFormRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AppliedFormService
{
   private final AppliedFormRepo appliedFormRepo;

   @Autowired
    public AppliedFormService(AppliedFormRepo appliedFormRepo) {
        this.appliedFormRepo = appliedFormRepo;
    }


    public ResponseEntity completeForm(String userId,String formId){
        AppliedForm appliedForm=appliedFormRepo.findByUserIdAndFormId(userId,formId);
        appliedForm.setStatus(AppliedForm.Status.COMPLETED);
        return new ResponseEntity(appliedFormRepo.save(appliedForm),HttpStatus.OK);
    }

    public ResponseEntity create(AppliedForm appliedForm) {
        try {
            appliedForm.setStatus(AppliedForm.Status.PENDING);
            appliedForm.setCreateAt(new Date());
            appliedForm.setModifiedAt(new Date());
            return new ResponseEntity(appliedFormRepo.save(appliedForm), HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity(ExceptionConstants.DATAALREADYEXIST.value, HttpStatus.BAD_REQUEST);
        }
    }

    public List<AppliedForm> findAllByUserId(String userId){
        return appliedFormRepo.findAllByUserId(userId);
    }
}
