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
    private String fieldName;
    private String filedValue;

    public UserData(String userId,String fieldName,String fieldValue)
    {
        this.setUserId(userId);
        this.setFieldName(fieldName);
        this.setFiledValue(fieldValue);
    }
}
