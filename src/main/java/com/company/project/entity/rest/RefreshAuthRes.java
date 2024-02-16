package com.company.project.entity.rest;

import lombok.Data;

/**
 * @author : Cody.Teng
 * @date : 2024-02-14 1:57 p.m.
 */
@Data
public class RefreshAuthRes {

    private String access_token;
    private String refresh_token;
    private String api_domain;
    private String token_type;
    private String expires_in;
    private String ecshopId;

}
