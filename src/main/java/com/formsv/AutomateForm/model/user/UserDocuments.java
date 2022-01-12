package com.formsv.AutomateForm.model.user;


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
@Document(collection = "userDocument")
@CompoundIndexes({
        @CompoundIndex(name = "userId_documentId", def = "{'userId' : 1, 'documentId': 1}")
})
public class UserDocuments {
    @Id
    private String _id;
    private String userId;
    private String documentId;
    private String imageId;
}
