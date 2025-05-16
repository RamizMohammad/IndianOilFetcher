package com.indian.ramiz.indianoilfetrcher.apiendpoints;

public class PostResponse {

    private Boolean status;

    public PostResponse(Boolean status) {
        this.status = status;
    }

    public PostResponse() {
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
