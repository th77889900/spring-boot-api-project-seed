package com.company.project.entity.rest;

import lombok.Data;

/**
 * @author : Cody.Teng
 * @date : 2024-02-14 1:54 p.m.
 */
@Data
public class InvSyncParamEntity {
    private Integer locationId;
    private Integer productId;
    private Integer quantity;
    private String reason;
    private CostParamInfo cost;
}
