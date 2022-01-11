package com.formsv.AutomateForm.repository.form;

import com.formsv.AutomateForm.model.form.AppliedForm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppliedFormRepo extends MongoRepository<AppliedForm,String> {

   // List<AppliedForm>  getAllBy_idIsNotNullAndOrderByCreateAtAsc();
    AppliedForm findByUserIdAndFormId(String userId,String formId);

}
