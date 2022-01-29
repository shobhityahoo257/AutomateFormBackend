package com.formsv.AutomateForm.service;

import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.FormRequiredDocument;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.repository.SupportedDocRepo;
import com.formsv.AutomateForm.repository.form.FormRequiredDocumentRepo;
import com.formsv.AutomateForm.repository.user.UserDocumentsRepo;
import com.formsv.AutomateForm.service.form.FormRequiredDocumentService;
import com.formsv.AutomateForm.service.form.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SupportedDocService {

    private final SupportedDocRepo supportedDocRepo;
    private final FormRequiredDocumentService formRequiredDocumentService;
    private final FormService formService;
    private final UserDocumentsRepo userDocumentsRepo;
    private final FormRequiredDocumentRepo formRequiredDocumentRepo;

    @Autowired
    public SupportedDocService(SupportedDocRepo supportedDocRepo, FormRequiredDocumentService formRequiredDocumentService, FormService formService, UserDocumentsRepo userDocumentsRepo, FormRequiredDocumentRepo formRequiredDocumentRepo) {
        this.supportedDocRepo = supportedDocRepo;
        this.formRequiredDocumentService = formRequiredDocumentService;
        this.formService = formService;
        this.userDocumentsRepo = userDocumentsRepo;
        this.formRequiredDocumentRepo = formRequiredDocumentRepo;
    }


    public String createSupportedDoc(SupportedDoc supportedDoc) throws Exception {
        try {
            SupportedDoc l = supportedDocRepo.save(supportedDoc);
            return l.get_id();
        }
        catch (org.springframework.dao.DuplicateKeyException e) {
            throw new Exception(ExceptionConstants.DATAALREADYEXIST.value);
        }
    }

    public boolean isAllDocumentExists(List<String> ids)
    {
        if(supportedDocRepo.findAllBy_idIsIn(ids).size()==ids.size())
            return true;
         return false;
    }

    public ResponseEntity addSupportedDocumentforForm(String formId, List<FormRequiredDocument> rdoc) throws  Exception{
        //Validate if Form Exist
        if(!formService.isFormExist(formId) )
            return new ResponseEntity("Form Doesn't Exist",HttpStatus.BAD_REQUEST);

       // formRequiredDocumentService.saveAll(rdoc);
        int i=0;
        List<String> ids=new ArrayList<>();
        for (FormRequiredDocument doc:rdoc) {
            ids.add(doc.getDocumentId());
            rdoc.get(i).setFormId(formId);
            i++;
        }
            if(isAllDocumentExists(ids)) {
                try {
                    return new ResponseEntity(formRequiredDocumentService.saveAll(rdoc), HttpStatus.CREATED);
                } catch (org.springframework.dao.DuplicateKeyException e){
                return new ResponseEntity("Data Already Exist", HttpStatus.BAD_REQUEST);
                }
            }
            else
                return new ResponseEntity("One Of the Document is not Supported",HttpStatus.BAD_REQUEST);
    }

    public void deleteRequiredDocument(String formId,List<FormRequiredDocument> requiredDocuments) throws Exception{
            if(requiredDocuments==null || requiredDocuments.size()==0)
                return;
        for (int i=0;i< requiredDocuments.size();i++) {
            requiredDocuments.get(i).setFormId(formId);
            if(requiredDocuments.get(i).get_id()==null)
                throw new Exception("_id Cant be null");
        }
        formRequiredDocumentRepo.deleteAll(requiredDocuments);
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

    /**
     * Generally This function should not be used
     * @param documentId
     */
    public void deleteSupportedDocument(String documentId){

    }

    public SupportedDoc updateSupportedDoc(SupportedDoc doc){
       return supportedDocRepo.save(doc);
    }

    public List<SupportedDoc> findAllBy_idIsIn(List<String > ids)
    {
        return supportedDocRepo.findAllBy_idIsIn(ids);
    }

    public List<SupportedDoc> findAll(){
        return supportedDocRepo.findAll();
    }

    public  String findDocumentId(String documentName){
        return supportedDocRepo.findByDocName(documentName).get_id();
    }
}
