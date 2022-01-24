package com.formsv.AutomateForm.model.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Setter
@Document(collection = "userData")
@NoArgsConstructor
public class UserData {
    @Id
    private String id;
    @Indexed
    private String userId;
    private String documentId;
    private String fieldId;
    private String value;

    public UserData(String userId,String fieldId,String value)
    {
        this.setUserId(userId);
        this.setFieldId(fieldId);
        this.setValue(value);
    }
}
