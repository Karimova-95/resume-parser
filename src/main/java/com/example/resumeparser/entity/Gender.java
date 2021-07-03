package com.example.resumeparser.entity;

public enum Gender {
    female, male;

    public static Gender fromString(String str) {
        if (str.equals("Мужской"))
            return male;
        else if (str.equals("Женский"))
            return female;
        return null;
    }

    public static String fromGender(Gender gender) {
        if (gender.equals(Gender.female))
            return "Женский";
        else if (gender.equals(Gender.male))
            return "Мужской";
        return null;
    }
}