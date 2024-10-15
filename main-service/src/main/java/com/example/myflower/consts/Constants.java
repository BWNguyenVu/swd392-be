package com.example.myflower.consts;

public class Constants {
    private Constants() {}

    public static final String DEFAULT_USER_AVATAR = "logo.png";

    public static final String SORT_ORDER_DESCENDING = "desc";

    public static final String SORT_FLOWER_LISTING_BY_NAME = "name";
    public static final String SORT_FLOWER_LISTING_BY_PRICE = "price";
    public static final String SORT_FLOWER_LISTING_BY_CREATE_DATE = "createdAt";

    public static final Integer S3_PRESIGNED_URL_EXPIRATION_MILISECONDS = 1000 * 60 * 60 * 24 * 7;
}