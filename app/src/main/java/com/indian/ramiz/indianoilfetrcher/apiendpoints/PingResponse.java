package com.indian.ramiz.indianoilfetrcher.apiendpoints;

public class PingResponse {

    private String message;
    private Boolean status;

    public PingResponse(String message, Boolean status) {
        this.message = message;
        this.status = status;
    }

    public PingResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
