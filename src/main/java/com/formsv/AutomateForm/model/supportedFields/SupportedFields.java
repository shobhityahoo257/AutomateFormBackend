package com.formsv.AutomateForm.model.supportedFields;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@AllArgsConstructor
@Getter
@Setter
@Document(collation = "supportedFields")
public class
SupportedFields {
    @Id
    private String _id;
    @Indexed(unique = true,background = true)
    private String fieldName;
}

