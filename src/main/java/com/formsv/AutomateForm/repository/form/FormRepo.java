package com.formsv.AutomateForm.repository.form;



import com.formsv.AutomateForm.model.form.Form;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepo extends MongoRepository<Form,String> {
    Optional<Form> findBy_id(String id);
}
