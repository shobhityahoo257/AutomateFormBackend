package com.formsv.AutomateForm.service.form;


import com.formsv.AutomateForm.Constants.Constants;
import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.AppliedForm;
import com.formsv.AutomateForm.model.form.Form;
import com.formsv.AutomateForm.repository.form.FormRepo;
import com.formsv.AutomateForm.responseModel.UserFormResponse;
import com.formsv.AutomateForm.responseModel.employeeResponseModel.AllFormData;
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

    private final FormRepo formRepo;
    private final AppliedFormService appliedFormRepoService;
    private final FormRequiredDocumentService formRequiredDocumentService;

    @Autowired
    public FormService(FormRepo formRepo, AppliedFormService appliedFormRepoService, FormRequiredDocumentService formRequiredDocumentService) {
        this.formRepo = formRepo;
        this.appliedFormRepoService = appliedFormRepoService;
        this.formRequiredDocumentService = formRequiredDocumentService;
    }


    /**
     * This Returns all forms currently enabled from Our Database Forms have the flag userApplied which shows if User has Applied form or not
     * @param userId
     * @return
     * @throws Exception
     */
    public ResponseEntity getAllFormsOfUser(String userId) throws Exception{


        //Here FormList size is not more than 1000. So not much concern about complexity
        List<Form> formList=formRepo.findByEnabledOrderByCreatedAtDesc(true);

        List<AppliedForm> appliedForms=appliedFormRepoService.findAllByUserId(userId);

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

    public ResponseEntity getAllForms() throws Exception{
        List<Form> formList=formRepo.findAll();
        return new ResponseEntity(formList,HttpStatus.OK);
    }

    public ResponseEntity updateFormData(String formId,Form form){
        if(!isFormExist(formId))
            return new ResponseEntity(ExceptionConstants.FORMNOTEXIST,HttpStatus.BAD_REQUEST);
        return new ResponseEntity(formRepo.save(form),HttpStatus.OK);
    }

    public ResponseEntity deleteForm(String formId) throws Exception{
        formRepo.deleteById(formId);
        formRequiredDocumentService.deleteAllRequiredDocumentsOfFOrm(formId);
        return new ResponseEntity(Constants.DELETED,HttpStatus.OK);
    }


}
