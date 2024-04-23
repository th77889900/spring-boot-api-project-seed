package com.company.project.entity.rest;

import lombok.Data;

import java.util.Map;

/**
 * @author : Cody.Teng
 * @date : 2024-02-14 1:54 p.m.
 */
@Data
public class ProductAvailResEntity {
    private Map<String, StockAvailResEntity> warehouses;
}
