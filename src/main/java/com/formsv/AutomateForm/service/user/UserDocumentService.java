package com.formsv.AutomateForm.service.user;


import com.formsv.AutomateForm.model.image.Image;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.repository.SupportedDocRepo;
import com.formsv.AutomateForm.repository.user.UserDocumentsRepo;
import com.formsv.AutomateForm.service.SupportedDocService;
import com.formsv.AutomateForm.service.image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserDocumentService {

    private final  UserDocumentsRepo userDocumentsRepo;
    private final   SupportedDocService supportedDocService;
    private final ImageService imageService;
    private final SupportedDocRepo supportedDocRepo;

    @Autowired
    public UserDocumentService(UserDocumentsRepo userDocumentsRepo, SupportedDocService supportedDocService, ImageService imageService, SupportedDocRepo supportedDocRepo) {
        this.userDocumentsRepo = userDocumentsRepo;
        this.supportedDocService = supportedDocService;
        this.imageService = imageService;
        this.supportedDocRepo = supportedDocRepo;
    }

    public ResponseEntity addDocument(String userId, String documentId, MultipartFile fileFront,MultipartFile fileBack) throws Exception {
        SupportedDoc supportedDoc=supportedDocService.getById(documentId);
        if(supportedDoc==null)
            return new ResponseEntity("Document Doesn't Exist with given documentId", HttpStatus.BAD_REQUEST);
        UserDocuments doc = new UserDocuments();
        doc.setDocumentId(documentId);
        if(fileFront!=null)
        doc.setImageIdFront( imageService.addImage(fileFront));
        doc.setImageIdBack( imageService.addImage(fileBack));
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
        List<String> ids=new ArrayList<>();
        for(UserDocuments d:doc)
        {
            ids.add(d.getDocumentId());
        }
      List<SupportedDoc> l=  supportedDocRepo.findAllBy_idIsIn(ids);
        Map<String,String> map=new HashMap<>();
        for (SupportedDoc sd:l){
            map.put(sd.get_id(),sd.getDocName());
        }
         for(int i=0;i< doc.size();i++)
             doc.get(i).setDocumentName(map.get(doc.get(i).getDocumentId()));
        return doc;
    }

    public void deleteDocumentById (String docId) throws Exception
    {
        userDocumentsRepo.deleteById(docId);
    }

    public UserDocuments updateUserDocument(String userId, String documentId, MultipartFile f) throws IOException {
        UserDocuments ud=userDocumentsRepo.getByUserIdAndDocumentId(userId,documentId);
        Image image=new Image(ud.getImageID(),f.getBytes());
        imageService.saveImage(image);
        return ud;
    }

    public List<UserDocuments> findByUserId(String userId){
        return userDocumentsRepo.findByUserId(userId);
    }


}
