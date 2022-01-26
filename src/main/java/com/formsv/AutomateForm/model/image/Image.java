package com.formsv.AutomateForm.model.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "image")
public class Image {
    @Id
    private String id;
    private byte[] image;

    public Image(MultipartFile multipartFile) throws IOException {
        image=multipartFile.getBytes();
    }
}