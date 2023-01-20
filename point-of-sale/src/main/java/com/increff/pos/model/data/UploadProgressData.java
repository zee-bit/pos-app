package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UploadProgressData {

    private Integer totalCount;
    private Integer successCount;
    private Integer errorCount;
    private List<String> errorMessages;

    public UploadProgressData() {
        this.totalCount = 0;
        this.successCount = 0;
        this.errorCount = 0;
        this.errorMessages = new ArrayList<String>();
    }
}
