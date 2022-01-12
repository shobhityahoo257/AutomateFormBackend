package com.formsv.AutomateForm.model.form;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "form")
public class Form {
    @Id
    private String _id;
    @Indexed(unique = true)
    private String formName;
    private String searchTags;
    @NonNull
    private LocalDate applicationBeginDate;
    @NonNull
    private LocalDate lastDate;
    @NonNull()
    private Integer formFee;
    @NonNull
    private Integer fillingCharge;
    @NonNull
    private boolean enabled;
    private boolean isUserApplied;
    private String formLink;

    private Date createdAt;

    private Date modifiedAt;
}
