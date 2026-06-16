package com.support.desk.service;

public class JwtResponse {

    private String jwtToken;

    public JwtResponse() {
    }

    private JwtResponse(Builder b) {
        this.jwtToken = b.jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String jwtToken;

        public Builder jwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
            return this;
        }

        public JwtResponse build() {
            return new JwtResponse(this);
        }
    }

}
