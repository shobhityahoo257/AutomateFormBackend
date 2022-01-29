package com.formsv.AutomateForm.repository;


import com.formsv.AutomateForm.model.supportedFields.SupportedFields;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportedFieldsRepo extends MongoRepository<SupportedFields,String> {
   SupportedFields findBy_id(String id);
   List<SupportedFields> findAllByFieldNameIsIn(List<String> list);
   List<SupportedFields> findAllByDocumentId(String documentId);
   void deleteAllByDocumentId(String id);



}
