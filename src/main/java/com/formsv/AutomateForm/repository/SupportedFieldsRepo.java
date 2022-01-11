package com.formsv.AutomateForm.repository;


import com.formsv.AutomateForm.model.supportedFields.SupportedFields;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupportedFieldsRepo extends MongoRepository<SupportedFields,String> {
   Optional<SupportedFields> findAllByFieldNameIsNotNull();
   SupportedFields findBy_id(String id);
}
