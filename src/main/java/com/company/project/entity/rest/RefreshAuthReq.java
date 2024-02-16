package com.company.project.entity.rest;

import lombok.Data;

/**
 * @author : Cody.Teng
 * @date : 2024-02-14 1:54 p.m.
 */
@Data
public class RefreshAuthReq {
    private String grant_type;
    private String refresh_token;
    private String client_id;
    private String account_code;
    private String ecshopId;
}
