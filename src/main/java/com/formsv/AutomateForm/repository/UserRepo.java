package com.formsv.AutomateForm.repository;


import com.formsv.AutomateForm.model.form.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends MongoRepository<User,String> {

    User findUserByMobileNumberAndUserName(String mobileNumber,String userName);
    User findUserByMobileNumberAndNoOfMembersIsGreaterThan(String mobileNumber,Long num);
    User findUserByMobileNumberAndNoOfMembersIsNotNull(String mobileNumber);
    User findUserBy_id(String id);
}
