package com.formsv.AutomateForm.model.form;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "appliedForm")
@CompoundIndexes({
        @CompoundIndex(name = "formId_userId", def = "{'formId' : 1, 'userId': 1}")
})
public class AppliedForm {
    @Id
    private String _id;
    private String formId;
    private String userId;
    private Date createAt;
    private Date modifiedAt;
    private Status status;

    public enum Status{
        PENDING,PARTIALYCOMPLETED,COMPLETED
    }
}
