package com.formsv.AutomateForm.model.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Setter
@Document(collection = "userData")
@CompoundIndexes({
        @CompoundIndex(name = "userName_mobileNumber", def = "{'userId' : 1, 'fieldId': 1}")
})
public class UserData {
    @Id
    private String _id;
    private String userId;
    private String fieldId;
    private String fieldValue;
}
