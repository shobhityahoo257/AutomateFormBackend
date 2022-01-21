package com.formsv.AutomateForm.repository.user;

import com.formsv.AutomateForm.model.user.UserData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDataRepo extends MongoRepository<UserData,String> {
//   UserData findByUserIdAndFieldId(String userId,String fieldId);
   List<UserData> findAllByUserId(String userId);
   void deleteAllByUserIdAndAndFieldNameIsIn(String userId,List<String> fieldName );
}
