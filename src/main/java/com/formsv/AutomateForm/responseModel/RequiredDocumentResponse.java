package com.formsv.AutomateForm.responseModel;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class RequiredDocumentResponse {
    private String userId;
    private String formId;
    private List<Document> doc;

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    class Document{
        private String documentId;
        private String documentName;
        private boolean uploadedByUser;
    }
}





