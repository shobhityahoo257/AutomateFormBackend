package com.formsv.AutomateForm.repository.form;

import com.formsv.AutomateForm.model.form.FormRequiredDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRequiredDocumentRepo extends MongoRepository<FormRequiredDocument,String > {

}
