package com.formsv.AutomateForm.model.responseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompareUserDataWithFormRequirement {
    private String documentId;
    private String imageIdFront;
    private String imageIdBack;
    private String documentName;
    private boolean isExist;
    private boolean isEditable;
    private boolean isMandatory;
    List<Data> userData;

    public static class Data{
        private String fieldId;
        private String value;
        private String fieldName;
        private boolean isMandatory;
    }

}
