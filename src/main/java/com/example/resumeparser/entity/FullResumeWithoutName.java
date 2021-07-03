package com.example.resumeparser.entity;

import lombok.Data;

@Data
public class FullResumeWithoutName extends Resume {
    private String id;
    private Integer age;
    private String birth_date;
    private Gender gender;
    private String area;
    private String title;
    private Object[] contacts;
    private Photo photo;
    private Education[] education;
    private Language[] language;
    private Experience[] experience;
    private Object[] skill_set;
}
