package com.formsv.AutomateForm.model.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "requiredDocument")
public class FormRequiredDocument {
    @Id
    private String _id;
    private String formId;
    private String documentId;
    private String documentName;

    public FormRequiredDocument(String formId, String documentId){
           this.formId=formId;
           this.documentId=documentId;
    }
}
