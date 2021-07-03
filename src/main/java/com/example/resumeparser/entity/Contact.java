package com.example.resumeparser.entity;

import lombok.Data;

@Data
public abstract class Contact {
    private String type;
    private String value;
}
