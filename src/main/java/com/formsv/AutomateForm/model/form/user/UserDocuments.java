package com.formsv.AutomateForm.model.form.user;


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
@Document(collation = "userDocument")
@CompoundIndexes({
        @CompoundIndex(name = "userName_mobileNumber", def = "{'userId' : 1, 'documentNmae': 1}")
})
public class UserDocuments {
    @Id
    private String _id;
    private String userId;
    private String documentNmae;
    private String url;
}
