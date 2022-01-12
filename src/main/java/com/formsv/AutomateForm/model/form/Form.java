package com.formsv.AutomateForm.model.form;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@Document(collection = "form")
public class Form {
    @Id
    private String _id;
    @NonNull
    @Indexed(unique = true)
    private String formName;
    private String searchTags;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate applicationBeginDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate lastDate;
    @NonNull()
    private Long formFee;
    @NonNull
    private Long fillingCharge;
    @NonNull
    private boolean enabled;
    private boolean isUserApplied;
    private String formLink;

}
