package com.formsv.AutomateForm.repository.user;


import com.formsv.AutomateForm.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends MongoRepository<User,String> {

    User findUserByMobileNumberAndUserName(String mobileNumber,String userName);
  //  User findUserByMobileNumberAndNoOfMembersIsNotNull(String mobileNumber);
    User findUserBy_id(String id) throws UsernameNotFoundException;;
    List<User> findByMobileNumber(String mobileNumber);
}
