package com.company.project.entity.rest;

import lombok.Data;

@Data
public class OrderRow {
    private String rowId;
    private String productId;
    private String productName;
    private String productSku;
}