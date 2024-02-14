package com.company.project.entity.rest;

/**
 * @author : Cody.Teng
 * @date : 2024-02-14 1:54 p.m.
 */
public class RefreshAuthReq {

    private String grant_type;
    private String refresh_token;
    private String client_id;
    private String account_code;

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getAccount_code() {
        return account_code;
    }

    public void setAccount_code(String account_code) {
        this.account_code = account_code;
    }
}
