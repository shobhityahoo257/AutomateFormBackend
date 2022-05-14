package com.formsv.AutomateForm.repository.form;

import com.formsv.AutomateForm.model.form.FormRequiredDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRequiredDocumentRepo extends MongoRepository<FormRequiredDocument,String > {
    List<FormRequiredDocument>  findByFormId(String formId);
    void deleteAllByFormId(String list);
    void deleteByFormIdAndDocumentId(String formId,String documentId);

    FormRequiredDocument findByFormIdAndDocumentId(String formId,String documentId);
}
