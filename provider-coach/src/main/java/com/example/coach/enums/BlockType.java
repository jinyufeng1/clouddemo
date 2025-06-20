package com.example.coach.enums;

public enum BlockType {
    TYPE_TEXT(1,"text"),
    TYPE_IMAGE(2,"image"),
    TYPE_VIDEO(3,"video");

    private final int code;
    private final String name;

    BlockType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


    public static boolean isArticleContentType(String name) {
        if (TYPE_TEXT.getName().equals(name)) {
            return true;
        }
        if (TYPE_IMAGE.getName().equals(name)) {
            return true;
        }
        if (TYPE_VIDEO.getName().equals(name)) {
            return true;
        }
        return false;
    }

}
