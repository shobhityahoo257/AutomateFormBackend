package com.formsv.AutomateForm.service.image;

import com.formsv.AutomateForm.model.image.Image;
import com.formsv.AutomateForm.model.supportedFields.SupportedDoc;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.repository.image.ImageRepo;
import com.formsv.AutomateForm.repository.user.UserDataRepo;
import com.formsv.AutomateForm.repository.user.UserDocumentsRepo;
import com.formsv.AutomateForm.service.SupportedDocService;
import com.formsv.AutomateForm.service.user.UserService;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;


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
        UserDocuments doc= userDocumentsRepo.getByUserIdAndDocumentId(userId,documentId);

            if(doc==null) {
                doc=new UserDocuments();
                doc.setDocumentId(documentId);
                doc.setDocumentName(supportedDoc.getDocName());
                doc.setDocumentUrl(uploadFileToFirebaseStorage(file));
                doc.setUserId(userId);
            }
            else {
                deleteFileFromFirebaseStorage(doc.getDocumentUrl());
                doc.setDocumentUrl(uploadFileToFirebaseStorage(file));
            }
            try {
                return new ResponseEntity(userDocumentsRepo.save(doc), HttpStatus.CREATED);
            }catch (org.springframework.dao.DuplicateKeyException e){
                return new ResponseEntity("Document Already Exist with same Document Id",HttpStatus.BAD_REQUEST);
            }
    }

    public String uploadFileToFirebaseStorage(MultipartFile multipartFile) throws IOException {
        // Initialize Firebase Storage
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/java/com/formsv/AutomateForm/fillojafrontend-firebase-adminsdk-quj1e-af637e66bc.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        // Generate a unique file name
        String uniqueFileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();

        // Convert MultipartFile to a temporary File
        File tempFile = File.createTempFile("temp", null);
        multipartFile.transferTo(tempFile);


        // Define the file destination in Firebase Storage
        BlobId blobId = BlobId.of("fillojafrontend.appspot.com", "documents/" + uniqueFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(multipartFile.getContentType()).build();

        // Upload the file
        Blob blob = storage.create(blobInfo, new FileInputStream(tempFile));
        tempFile.delete();// Clean up temp file

        String downloadUrl = blob.getStorage().get(blob.getBlobId()).signUrl(365*1000, TimeUnit.DAYS).toString();

        return downloadUrl;
    }

    public boolean deleteFileFromFirebaseStorage(String fileUrl) throws IOException {
        // Parse the file URL to get the blob name


        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/java/com/formsv/AutomateForm/fillojafrontend-firebase-adminsdk-quj1e-af637e66bc.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        try {
            // Parse the file URL to get the blob name
            String[] urlParts = fileUrl.split("/");
            String blobName = urlParts[urlParts.length - 1];
            String fileName=urlParts[urlParts.length - 1].split("\\?")[0];

            // Define the BlobId using the blob name and bucket name
            BlobId blobId = BlobId.of("fillojafrontend.appspot.com", "documents/" +fileName);

            // Delete the file
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                System.out.println("File deleted successfully.");
                return true;

            } else {
                System.out.println("Failed to delete the file or the file does not exist.");
            }
        } catch (Exception e) {
            // Log any exceptions that might occur during the deletion process
            e.printStackTrace();
            System.out.println("Error deleting the file: " + e.getMessage());
        }
        return false;
    }

    public Image getPhoto(String id) {
        return photoRepo.findById(id).get();
    }

}