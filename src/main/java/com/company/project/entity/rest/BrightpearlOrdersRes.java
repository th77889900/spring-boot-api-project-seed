package com.company.project.entity.rest;

import java.util.List;

/**
 * brightpearl 订单列表数据响应entity
 *
 * @author : Cody.Teng
 * @date : 2024-02-14 9:13 a.m.
 */
public class BrightpearlOrdersRes {

    public Response response;

    public static class Response {
        public List<List<String>> results;
//        public MetaData metaData;
//        public Reference reference;

        public List<List<String>> getResults() {
            return results;
        }

        public void setResults(List<List<String>> results) {
            this.results = results;
        }
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
