package com.formsv.AutomateForm.model.form.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Setter
@Document(collation = "supportedFields")
@CompoundIndexes({
        @CompoundIndex(name = "userName_mobileNumber", def = "{'userName' : 1, 'mobileNumber': 1}")
})
public class SupportedFields {
    @Id
    private String _id;
    @Indexed(unique = true)
    private String fieldName;
}
