
package com.ooad.oj_backend.mybatis.entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;



@Data
@Getter
@Setter
public class Contest implements Serializable {
    int id;
    int classId;
    long startTime;
    long endTime;
    String title;
    String description;
    String creatorName;
    String creatorId;
}
