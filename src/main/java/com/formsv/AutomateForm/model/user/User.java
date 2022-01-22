package com.formsv.AutomateForm.model.user;


import lombok.*;
import org.bson.types.Binary;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@Document(collection = "user")
@CompoundIndexes({
        @CompoundIndex(name = "userName_mobileNumber", def = "{'userName' : 1, 'mobileNumber': 1}")
})
@NoArgsConstructor
public class User {
    @Id
    private String _id;
    @NonNull
    @Size(min=10, max=10)
    private String mobileNumber;
    @NonNull
    @Size(min=1, max=20)
    private String userName;
    private byte[] profileImage;
    private boolean parent;
    private boolean lock;
    private Date createdAt;
    private Date modifiedAt;

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }
}
