package com.example.resumeparser.entity;

import lombok.Data;

@Data
public class Experience {

    private String company;
    private String area;
    private String position;
    private String start;
    private String end;
    private String description;
}