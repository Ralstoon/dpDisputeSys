package com.seu.domian;


public class NormalUserUpload {
    private String personId;
    private String disputeStatus;
    private String fileType;
    private String fileDescription;
    private String url;

    public NormalUserUpload(String personId, String disputeStatus, String fileType, String fileDescription, String url) {
        this.personId = personId;
        this.disputeStatus = disputeStatus;
        this.fileType = fileType;
        this.fileDescription = fileDescription;
        this.url = url;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getDisputeStatus() {
        return disputeStatus;
    }

    public void setDisputeStatus(String disputeStatus) {
        this.disputeStatus = disputeStatus;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
