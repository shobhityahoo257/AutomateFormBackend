package com.formsv.AutomateForm.model.user;


import lombok.*;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Setter
@Document(collection = "user")
@CompoundIndexes({
        @CompoundIndex(name = "userName_mobileNumber", def = "{'userName' : 1, 'mobileNumber': 1}")
})
@NoArgsConstructor
public class User {
    @Id
    private String _id;
    @NonNull
    private String mobileNumber;
    @NonNull
    private String userName;
    private Binary profileImage;
    private boolean isParent;
}
