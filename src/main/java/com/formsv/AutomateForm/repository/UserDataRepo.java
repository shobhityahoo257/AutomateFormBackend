package com.formsv.AutomateForm.repository;

import com.formsv.AutomateForm.model.form.user.UserData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepo extends MongoRepository<UserData,String> {

}
