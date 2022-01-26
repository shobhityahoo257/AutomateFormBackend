package com.formsv.AutomateForm.service;


import com.formsv.AutomateForm.model.transaction.UserInteraction;
import com.formsv.AutomateForm.repository.interaction.UserInteractionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInteractionService  {

   private final UserInteractionRepo userInteractionRepo;

   @Autowired
    public UserInteractionService(UserInteractionRepo userInteractionRepo) {
        this.userInteractionRepo = userInteractionRepo;
    }

    public UserInteraction saveInteraction(UserInteraction userInteraction){
       return userInteractionRepo.save(userInteraction);
    }
}
