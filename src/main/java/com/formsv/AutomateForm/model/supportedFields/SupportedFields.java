package com.formsv.AutomateForm.model.supportedFields;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@AllArgsConstructor
@Getter
@Setter
@Document(collection = "supportedFields")
public class SupportedFields {
    @Id
    private String _id;
    @NonNull
    private String documentId;
    private String fieldName;
}

