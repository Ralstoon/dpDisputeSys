package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class Comment {
    @Id
    private String commentId;
    private String caseId;
    private String otherEvaluation;
    private String userId;
    private String mediatorEvalution;
    private String state;

    public Comment() {
    }

    public Comment(String commentId, String caseId, String otherEvaluation, String userId, String mediatorEvalution, String state) {
        this.commentId = commentId;
        this.caseId = caseId;
        this.otherEvaluation = otherEvaluation;
        this.userId = userId;
        this.mediatorEvalution = mediatorEvalution;
        this.state=state;
    }
}
