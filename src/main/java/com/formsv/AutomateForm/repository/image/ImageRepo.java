package com.formsv.AutomateForm.repository.image;

import com.formsv.AutomateForm.model.image.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepo extends MongoRepository<Image,String> {
}
