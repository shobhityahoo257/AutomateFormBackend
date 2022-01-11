package com.formsv.AutomateForm.service.image;

import com.formsv.AutomateForm.model.image.Image;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.repository.image.ImageRepo;
import com.formsv.AutomateForm.repository.user.UserDataRepo;
import com.formsv.AutomateForm.repository.user.UserDocumentsRepo;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class ImageService {

    @Autowired
    private ImageRepo photoRepo;
    @Autowired
    private UserDocumentsRepo userDocumentsRepo;

    public String addPhoto(String userId,String documentId,MultipartFile file) throws Exception {

            Image photo = new Image();
            UserDocuments doc = new UserDocuments();
            photo.setImage(
                    new Binary(BsonBinarySubType.BINARY, file.getBytes()));
            photo = photoRepo.insert(photo);
            doc.setDocumentId(documentId);
            doc.setImageId(photo.getId());
            doc.setUserId(userId);
            userDocumentsRepo.save(doc);
            return photo.getId();

    }

    public Image getPhoto(String id) {
        return photoRepo.findById(id).get();
    }
}