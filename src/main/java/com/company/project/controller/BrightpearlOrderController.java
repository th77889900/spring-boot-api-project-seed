package com.company.project.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/brightpearl/queen")
public class BrightpearlOrderController {

    @PostMapping("/orders")
    public List<String> getBatchOrder() {



        List<String> results = new ArrayList<>();

        results.add("order1");
        results.add("order2");


        return results;
    }
}
