package com.formsv.AutomateForm.repository.form;


import com.formsv.AutomateForm.model.form.AppliedForm;
import com.formsv.AutomateForm.model.form.Form;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepo extends MongoRepository<Form,String> {
}
