package com.indian.ramiz.indianoilfetrcher.apiendpoints;

public class SendBody {

    private String otp;
    private String date;
    private String time;

    public SendBody(String otp, String date, String time) {
        this.otp = otp;
        this.date = date;
        this.time = time;
    }

    public SendBody() {
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        time = time;
    }
}
