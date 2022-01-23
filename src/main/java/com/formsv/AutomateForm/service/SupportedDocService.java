package com.formsv.AutomateForm.service;

import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.FormIdsPojo;
import com.formsv.AutomateForm.model.form.FormRequiredDocument;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.repository.SupportedDocRepo;
import com.formsv.AutomateForm.repository.form.FormRequiredDocumentRepo;
import com.formsv.AutomateForm.repository.user.UserDocumentsRepo;
import com.formsv.AutomateForm.service.form.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SupportedDocService {
    @Autowired
    SupportedDocRepo supportedDocRepo;

    @Autowired
    FormRequiredDocumentRepo formRequiredDocumentRepo;
    @Autowired
    FormService formService;
    @Autowired
    UserDocumentsRepo userDocumentsRepo;
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


    public List<SupportedDoc> getAllSupportedDocuments(String userId){
        List<UserDocuments> userDocuments=userDocumentsRepo.findByUserId(userId);
        List<SupportedDoc> doc= supportedDocRepo.findAll();
        List<SupportedDoc> res=new ArrayList<>();
        if(doc==null || doc.size()==0)
            return doc;
        Set<String>  set=new HashSet<>();
        for (UserDocuments userDocuments1:userDocuments) {
            set.add(userDocuments1.getDocumentId());
        }

        for (int i=0;i<doc.size();i++) {
            if(!set.contains(doc.get(i).get_id())){
                 res.add(doc.get(i));
            }
        }
        return res;
    }

}
