package com.seu.form.VOForm;

public class NormalUserUploadListForm {
    String url;
    String disputeID;

    public NormalUserUploadListForm(String url, String disputeID) {
        this.url = url;
        this.disputeID = disputeID;
    }

    public NormalUserUploadListForm() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDisputeID() {
        return disputeID;
    }

    public void setDisputeID(String disputeID) {
        this.disputeID = disputeID;
    }
}
