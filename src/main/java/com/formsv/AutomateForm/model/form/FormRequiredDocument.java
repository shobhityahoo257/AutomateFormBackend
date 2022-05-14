package com.formsv.AutomateForm.model.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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

    //this should be not stored in DB should be calculated at Run Time only
    private String documentName;
    private List<String> fieldIds;
    //this should be not stored in DB should be calculated at Run Time only
    private List<String> fieldName;

    public FormRequiredDocument(String formId, String documentId,String documentName,List<String> fieldIds,List<String> fieldName){
           this.formId=formId;
           this.documentId=documentId;
           this.documentName=documentName;
           this.fieldIds=fieldIds;
           this.fieldName=fieldName;
    }
}
