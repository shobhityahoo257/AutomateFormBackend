package com.formsv.AutomateForm.repository;


import com.formsv.AutomateForm.model.form.user.SupportedFields;
import com.formsv.AutomateForm.model.form.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportedFieldsRepo extends MongoRepository<SupportedFields,String> {
   Optional<SupportedFields> findAllByFieldNameIsNotNull();
}
