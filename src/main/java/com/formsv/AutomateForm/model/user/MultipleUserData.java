package com.formsv.AutomateForm.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleUserData {
    @NonNull
    private String userId;
    @NonNull
    private String documentId;
    @NonNull
    private List<UserData> userDataList;

}
