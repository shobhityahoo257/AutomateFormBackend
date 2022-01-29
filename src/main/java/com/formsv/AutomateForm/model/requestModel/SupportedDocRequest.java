package com.formsv.AutomateForm.model.requestModel;

import com.formsv.AutomateForm.model.supportedFields.SupportedFields;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SupportedDocRequest {
    private String _id;
    private String docName;
    private List<SupportedFields> fieldsList;
}
