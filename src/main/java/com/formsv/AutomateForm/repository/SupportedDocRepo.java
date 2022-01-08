package com.formsv.AutomateForm.repository;

import com.formsv.AutomateForm.model.form.user.SupportedDoc;
import com.formsv.AutomateForm.model.form.user.SupportedFields;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportedDocRepo extends MongoRepository<SupportedDoc,String> {

}
