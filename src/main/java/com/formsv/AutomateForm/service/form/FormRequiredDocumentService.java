package com.formsv.AutomateForm.service.form;

import com.formsv.AutomateForm.model.form.FormRequiredDocument;
import com.formsv.AutomateForm.repository.form.FormRequiredDocumentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormRequiredDocumentService {

    private final FormRequiredDocumentRepo formRequiredDocumentRepo;

    @Autowired
    public FormRequiredDocumentService(FormRequiredDocumentRepo formRequiredDocumentRepo) {
        this.formRequiredDocumentRepo = formRequiredDocumentRepo;
    }

    public List<FormRequiredDocument>  findByFormId(String formId){
        return formRequiredDocumentRepo.findByFormId(formId);
    }

    public boolean deleteAllRequiredDocumentsOfFOrm(String formId){
        formRequiredDocumentRepo.deleteAllByFormId(formId);
        return true;
    }

    public void deleteAllByFormId(String formId){
        formRequiredDocumentRepo.deleteAllByFormId(formId);
    }

    public List<FormRequiredDocument>  saveAll(List<FormRequiredDocument> list)
    {
        return formRequiredDocumentRepo.saveAll(list);
    }
}
