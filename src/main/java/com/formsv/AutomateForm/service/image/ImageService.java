package com.formsv.AutomateForm.service.image;

import com.formsv.AutomateForm.model.image.Image;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.repository.image.ImageRepo;
import com.formsv.AutomateForm.repository.user.UserDataRepo;
import com.formsv.AutomateForm.repository.user.UserDocumentsRepo;
import com.formsv.AutomateForm.service.SupportedDocService;
import com.formsv.AutomateForm.service.user.UserService;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class ImageService {

    @Autowired
    private ImageRepo photoRepo;
    @Autowired
    private UserDocumentsRepo userDocumentsRepo;
    @Autowired
    UserService userService;
    @Autowired
    SupportedDocService supportedDocService;

    public ResponseEntity addPhoto(String userId, String documentId, MultipartFile file) throws Exception {
           if( !userService.isUserExistById(userId))
               return new ResponseEntity("User Doesn't Exist with given userId",HttpStatus.BAD_REQUEST);
        SupportedDoc supportedDoc=supportedDocService.getById(documentId);
           if(supportedDoc==null)
               return new ResponseEntity("Document Doesn't Exist with given documentId",HttpStatus.BAD_REQUEST);
            UserDocuments doc = new UserDocuments();
            doc.setDocumentId(documentId);
            doc.setDocumentName(supportedDoc.getDocName());
            doc.setImage( new Binary(BsonBinarySubType.BINARY, file.getBytes()));
            doc.setUserId(userId);
            try {
                return new ResponseEntity(userDocumentsRepo.save(doc), HttpStatus.CREATED);
            }catch (org.springframework.dao.DuplicateKeyException e){
                return new ResponseEntity("Document Alredy Exist with same Document Id",HttpStatus.BAD_REQUEST);
            }
    }

    public Image getPhoto(String id) {
        return photoRepo.findById(id).get();
    }

}