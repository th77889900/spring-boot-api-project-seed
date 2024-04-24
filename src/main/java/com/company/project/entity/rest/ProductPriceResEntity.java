package com.company.project.entity.rest;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author : Cody.Teng
 * @date : 2024-02-14 1:54 p.m.
 */
@Data
public class ProductPriceResEntity {
    private Integer productId;
    private List<CostInfoList> priceLists;
    public static class CostInfoList {
        private Map<String, String> quantityPrice;
        private String currencyCode;
        public Map<String, String> getQuantityPrice() {
            return quantityPrice;
        }

        public void setQuantityPrice(Map<String, String> quantityPrice) {
            this.quantityPrice = quantityPrice;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }
    }

}
