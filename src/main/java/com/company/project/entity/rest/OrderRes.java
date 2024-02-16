package com.company.project.entity.rest;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author : Cody.Teng
 * @date : 2024-02-14 4:54 p.m.
 */
@Data
public class OrderRes {

    private List<OrderResponse> response;
    private String ecshopId;


    @Data
    public static class OrderResponse {
        private String parentOrderId;
        private String orderTypeCode;
        private String orderStatusID;
        private String orderPaymentStatus;
        private String addressFullName;
        private String companyName;
        private String addressLine1;
        private String addressLine2;
        private String addressLine3;
        private String addressLine4;
        private String postalCode;
        private String country;
        private String telephone;
        private String mobileTelephone;
        private String email;
        private String countryId;
        private String countryIsoCode;
        private String createdOn;
        private String updateOn;
        private String oprSeqflag;
        private String organizationId;
        private String ecShopId;
        private String id;
        private Parties parties;
        private Map<String, OrderRow> orderRows;
        private List<OrderRow> orderRowList;
    }


    @Data
    public static class OrderRow {
        private String rowId;
        private String productId;
        private String productName;
        private String productSku;
        private BigDecimal qty;
        private BigDecimal itemPrice;
        private Quantity quantity;
        private ItemCost itemCost;
    }

    @Data
    public static class Quantity {
        private String magnitude;
    }

    @Data
    public static class ItemCost {
        private String currencyCode;
        private Double value;
    }

    @Data
    public static class Parties {
        private Delivery delivery;
    }

    @Data
    public static class Delivery {
        private String addressFullName;
        private String companyName;
        private String addressLine1;
        private String addressLine2;
        private String addressLine3;
        private String addressLine4;
        private String postalCode;
        private String country;
        private String telephone;
        private String mobileTelephone;
        private String fax;
        private String email;
        private String countryId;
        private String countryIsoCode;
        private String countryIsoCode3;
    }

    public void transferRowMap2List(List<OrderResponse> response) {
        if (CollectionUtils.isEmpty(response)) {
            return;
        }
        for (OrderResponse orderResponse : response) {
            Map<String, OrderRow> orderRows = orderResponse.getOrderRows();
            if (CollectionUtils.isEmpty(orderRows)) {
                continue;
            }
            List<OrderRow> rowList = new ArrayList<>();
            for (String rowId : orderRows.keySet()) {
                OrderRow row = orderRows.get(rowId);
                row.setRowId(rowId);
                rowList.add(row);
            }
            orderResponse.setOrderRowList(rowList);
        }
    }


    public void transferParties2Response(List<OrderResponse> orderResponses) {
        if (CollectionUtils.isEmpty(orderResponses)) {
            return;
        }
        for (OrderResponse response : orderResponses) {
            if (Objects.isNull(response.getParties())) {
                return;
            }
            Delivery delivery = response.getParties().getDelivery();
            if (Objects.isNull(delivery)) {
                return;
            }
            response.setAddressFullName(delivery.addressFullName);
            response.setCompanyName(delivery.companyName);
            response.setAddressLine1(delivery.addressLine1);
            response.setAddressLine2(delivery.addressLine2);
            response.setAddressLine3(delivery.addressLine3);
            response.setAddressLine4(delivery.addressLine4);
            response.setPostalCode(delivery.postalCode);
            response.setCountry(delivery.country);
            response.setTelephone(delivery.telephone);
            response.setMobileTelephone(delivery.mobileTelephone);
            response.setEmail(delivery.email);
            response.setCountryId(delivery.countryId);
            response.setCountryIsoCode(delivery.countryIsoCode);
        }
    }


    public void transferRowQtyAndPrice(List<OrderResponse> orderResponses) {
        if (CollectionUtils.isEmpty(orderResponses)) {
            return;
        }
        for (OrderResponse response : orderResponses) {
            List<OrderRow> orderRowList = response.getOrderRowList();
            if (CollectionUtils.isEmpty(orderRowList)) {
                continue;
            }
            for (OrderRow row : orderRowList) {
                Quantity quantity = row.getQuantity();
                if (Objects.isNull(quantity)) {
                    continue;
                }
                String magnitude = quantity.getMagnitude();
                BigDecimal qty = new BigDecimal(magnitude);
                qty = qty.setScale(2, RoundingMode.HALF_UP);
                row.setQty(qty);
                ItemCost itemCost = row.getItemCost();
                if (Objects.isNull(itemCost)) {
                    continue;
                }
                Double value = itemCost.value;
                BigDecimal price = new BigDecimal(value);
                price = price.setScale(2, RoundingMode.HALF_UP);
                row.setItemPrice(price);
            }
        }
    }

}
