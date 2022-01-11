package com.formsv.AutomateForm.repository.user;

import com.formsv.AutomateForm.model.user.UserDocuments;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDocumentsRepo extends MongoRepository<UserDocuments,String> {
    UserDocuments getByUserIdAndDocumentId(String userId,String documentId);
    UserDocuments getBy_id(String id);
}
