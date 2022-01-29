package com.formsv.AutomateForm.service;

import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.supportedFields.SupportedFields;
import com.formsv.AutomateForm.repository.SupportedFieldsRepo;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupportedFieldsService {

    private final SupportedFieldsRepo supportedFieldsRepo;

    @Autowired
    public SupportedFieldsService(SupportedFieldsRepo supportedFieldsRepo) {
        this.supportedFieldsRepo = supportedFieldsRepo;
    }

    public ResponseEntity createSupportedFields(List<SupportedFields> list,String documentId) {
        for(int i=0;i<list.size();i++)
            list.get(i).setDocumentId(documentId);
        try {
            List<SupportedFields> l = supportedFieldsRepo.insert(list);
            return new ResponseEntity(l, HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity(ExceptionConstants.DATAALREADYEXIST.value, HttpStatus.BAD_REQUEST);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            return new ResponseEntity(ExceptionConstants.DATAALREADYEXIST.value, HttpStatus.BAD_REQUEST);
        }
    }

    public boolean isFieldExist(String id){
     if(supportedFieldsRepo.findBy_id(id)==null)
         return false;
     return true;
    }

   public List<SupportedFields> findAllByFieldNameIsIn(List<String > fieldsName){
       return   supportedFieldsRepo.findAllByFieldNameIsIn(fieldsName);
   }


   public List<SupportedFields> findAllByDocumentId(String documentId){
        return supportedFieldsRepo.findAllByDocumentId(documentId);
   }

   public void deleteAllByDocumentId(String documentId){
        supportedFieldsRepo.deleteAllByDocumentId(documentId);
   }
}
