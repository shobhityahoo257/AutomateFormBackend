package com.formsv.AutomateForm.repository;

import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportedDocRepo extends MongoRepository<SupportedDoc,String> {

}
