package com.company.project.entity.rest;

import lombok.Data;

/**
 * @author : Cody.Teng
 * @date : 2024-02-14 1:54 p.m.
 */
@Data
public class OrderCloseReq {
    private String token;
    private String brightpearlDevRef;
    private String brightpearlAppRef;
    private String orderId;
}
