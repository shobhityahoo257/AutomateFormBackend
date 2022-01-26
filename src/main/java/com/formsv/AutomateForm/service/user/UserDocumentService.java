package com.formsv.AutomateForm.service.user;


import com.formsv.AutomateForm.model.image.Image;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.repository.user.UserDocumentsRepo;
import com.formsv.AutomateForm.service.SupportedDocService;
import com.formsv.AutomateForm.service.image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UserDocumentService {

    private final  UserDocumentsRepo userDocumentsRepo;
    private final   SupportedDocService supportedDocService;
    private final ImageService imageService;

    @Autowired
    public UserDocumentService(UserDocumentsRepo userDocumentsRepo, SupportedDocService supportedDocService, ImageService imageService) {
        this.userDocumentsRepo = userDocumentsRepo;
        this.supportedDocService = supportedDocService;
        this.imageService = imageService;
    }

    public ResponseEntity addDocument(String userId, String documentId, MultipartFile file) throws Exception {
        SupportedDoc supportedDoc=supportedDocService.getById(documentId);
        if(supportedDoc==null)
            return new ResponseEntity("Document Doesn't Exist with given documentId", HttpStatus.BAD_REQUEST);
        UserDocuments doc = new UserDocuments();
        doc.setDocumentId(documentId);
        doc.setDocumentName(supportedDoc.getDocName());
        doc.setImage( imageService.addImage(file));
        doc.setUserId(userId);
        try {
            return new ResponseEntity(userDocumentsRepo.save(doc), HttpStatus.CREATED);
        }catch (org.springframework.dao.DuplicateKeyException e){
            return new ResponseEntity("Document Already Exist with same Document Id",HttpStatus.BAD_REQUEST);
        }
    }


    public List<UserDocuments> getAllUserDocuments(String userId){
        List<UserDocuments> doc= userDocumentsRepo.findByUserId(userId);
        if(doc==null|| doc.size()==0)
            return doc;
        for (int i=0;i<doc.size();i++)
        {
            doc.get(i).setImage(null);
        }
        return doc;
    }

    public void deleteDocumentById (String docId) throws Exception
    {
        userDocumentsRepo.deleteById(docId);
    }

    public UserDocuments updateUserDocument(String userId, String documentId, MultipartFile f) throws IOException {
        UserDocuments ud=userDocumentsRepo.getByUserIdAndDocumentId(userId,documentId);
        Image image=new Image(ud.getImage(),f.getBytes());
        imageService.saveImage(image);
        return ud;
    }

    public List<UserDocuments> findByUserId(String userId){
        return userDocumentsRepo.findByUserId(userId);
    }

}
