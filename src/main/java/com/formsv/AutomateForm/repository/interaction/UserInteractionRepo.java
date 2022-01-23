package com.formsv.AutomateForm.repository.interaction;

import com.formsv.AutomateForm.model.transaction.UserInteraction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInteractionRepo extends MongoRepository<UserInteraction, String> {

}
