package com.formsv.AutomateForm.service.form;


import com.formsv.AutomateForm.model.form.AppliedForm;
import com.formsv.AutomateForm.model.form.Form;
import com.formsv.AutomateForm.repository.form.AppliedFormRepo;
import com.formsv.AutomateForm.repository.form.FormRepo;
import com.formsv.AutomateForm.responseModel.UserFormResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FormService {
    @Autowired
    FormRepo formRepo;
    @Autowired
    AppliedFormRepo appliedFormRepo;
    public ResponseEntity getAllFormsOfUser(String userId) throws Exception{
        List<Form> formList=formRepo.findAll();
        List<AppliedForm> appliedForms=appliedFormRepo.findAllByUserId(userId);

        Set<String> set = new HashSet<String >();
        for (AppliedForm appliedForm:appliedForms) {
          set.add(appliedForm.getFormId());
        }
        for (Form f:formList) {
            if(set.contains(f.get_id()))
                f.setUserApplied(true);
            else
                f.setUserApplied(false);
        }
        UserFormResponse userFormResponse=new UserFormResponse();
                userFormResponse.setUserId(userId);
                userFormResponse.setFormList(formList);
                return new ResponseEntity(userFormResponse, HttpStatus.OK);
    }


    public ResponseEntity createForm(Form f) throws Exception{
        try{
       return new ResponseEntity(formRepo.save(f),HttpStatus.CREATED);
    }catch (org.springframework.dao.DuplicateKeyException e)
        {
            return new ResponseEntity("Form Already Exist With Same Name",HttpStatus.FOUND);
        }
    }

    public boolean isFormExist(String id){
        Optional<Form> l=formRepo.findById(id);
        if(!l.isPresent())
            return false;
        return true;
    }
}