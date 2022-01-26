package com.formsv.AutomateForm.service.image;

import com.formsv.AutomateForm.model.image.Image;
import com.formsv.AutomateForm.repository.image.ImageRepo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;



@Service
public class ImageService {


    private final ImageRepo imageRepo;

    @Autowired
    public ImageService(ImageRepo imageRepo) {
        this.imageRepo = imageRepo;
    }


    public Image getImage(String id) {
        return imageRepo.findById(id).get();
    }

    public String addImage(MultipartFile multipartFile) throws IOException {
        return imageRepo.save(new Image(multipartFile)).getId();
    }

    public void deleteImage(String id){
        imageRepo.deleteById(id);
    }

    public Image saveImage(Image image){
       return imageRepo.save(image);
    }

}