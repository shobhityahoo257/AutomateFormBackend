package com.formsv.AutomateForm.model.form.user;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Setter
@Document(collation = "user")
@CompoundIndexes({
        @CompoundIndex(name = "userName_mobileNumber", def = "{'userName' : 1, 'mobileNumber': 1}")
})
public class User {
    @Id
    private String _id;
    @NonNull
    private String mobileNumber;
    private Long noOfMembers=1L;
    private String userName;
}
