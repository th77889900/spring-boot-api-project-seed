package com.company.project.entity.rest;

import lombok.Data;

import java.util.Map;

/**
 * @author : Cody.Teng
 * @date : 2024-02-14 1:54 p.m.
 */
@Data
public class StockAvailResEntity {
    private Integer inStock;
    private Integer onHand;
    private Integer allocated;
    private Integer inTransit;
}
