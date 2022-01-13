package com.formsv.AutomateForm.model.form;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FormIdsPojo {
     private List<String> ids;
     List<SupportedDocument> list;

     @Data
     @NoArgsConstructor
     @AllArgsConstructor
     @Getter
     @Setter
     static public class SupportedDocument{
          private String documentId;
          private String documentName;
     }
}

