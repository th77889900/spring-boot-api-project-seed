package com.company.project.entity.rest;

import lombok.Data;

import java.util.Map;

/**
 * brightpearl 订单列表数据响应entity
 *
 * @author : Cody.Teng
 * @date : 2024-02-14 9:13 a.m.
 */

@Data
public class BrightpearlProductAvailRes {

    public Map<String,ProductAvailResEntity> response;

}
