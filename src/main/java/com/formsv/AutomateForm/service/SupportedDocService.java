package com.formsv.AutomateForm.service;

import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.FormIdsPojo;
import com.formsv.AutomateForm.model.form.FormRequiredDocument;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.repository.SupportedDocRepo;
import com.formsv.AutomateForm.repository.form.FormRequiredDocumentRepo;
import com.formsv.AutomateForm.service.form.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupportedDocService {
    @Autowired
    SupportedDocRepo supportedDocRepo;

    @Autowired
    FormRequiredDocumentRepo formRequiredDocumentRepo;
    @Autowired
    FormService formService;

    public ResponseEntity createSupportedDoc(List<SupportedDoc> list) {
        try {
            List<SupportedDoc> l = supportedDocRepo.insert(list);
            return new ResponseEntity(l, HttpStatus.CREATED);
        }
        catch (org.springframework.dao.DuplicateKeyException e) {
            return new ResponseEntity(ExceptionConstants.DATAALREADYEXIST.value, HttpStatus.BAD_REQUEST);
        }
    }

    public boolean isAllDocumentExists(List<String> ids)
    {
        if(supportedDocRepo.findAllBy_idIsIn(ids).size()==ids.size())
            return true;
         return false;
    }

    public ResponseEntity addSupportedDocumentforForm(String formId, FormIdsPojo rdoc) throws  Exception{
        //Validate if Form Exist
        if(!formService.isFormExist(formId) )
            return new ResponseEntity("Form Doesn't Exist",HttpStatus.BAD_REQUEST);
        //Validate if Form Exist
        if(!formService.isFormExist(formId) )
            return new ResponseEntity("Form Doesn't Exist",HttpStatus.BAD_REQUEST);
        formRequiredDocumentRepo.deleteAllByFormId(formId);

        List<String> ids=new ArrayList<>();
        for (FormIdsPojo.SupportedDocument doc:rdoc.getList()) {
            ids.add(doc.getDocumentId());
        }

        List<FormRequiredDocument> formRequiredDocument=new ArrayList<>();

            if(isAllDocumentExists(ids)) {
                for (int i=0;i<ids.size();i++) {
                    formRequiredDocument.add(new FormRequiredDocument(formId, ids.get(i),rdoc.getList().get(i).getDocumentName()));
                }
                try {
                    return new ResponseEntity(formRequiredDocumentRepo.saveAll(formRequiredDocument), HttpStatus.CREATED);
                } catch (org.springframework.dao.DuplicateKeyException e){
                return new ResponseEntity("Data Already Exist", HttpStatus.BAD_REQUEST);
                }

            }
            else
                return new ResponseEntity("One Of the Document is not Supported",HttpStatus.BAD_REQUEST);
    }

    public boolean isDocumentExistById(String id){
         if( supportedDocRepo.findBy_id(id)==null )
             return false;
         return true;
    }

    public SupportedDoc getById(String id){
        return supportedDocRepo.findBy_id(id);
    }



}
