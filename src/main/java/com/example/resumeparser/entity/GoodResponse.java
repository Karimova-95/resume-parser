package com.example.resumeparser.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodResponse {
    private Integer resultCode;
    private Resume resume;
}
