package com.powerFind.model.domain;

public enum PaymentEnum
{
    PENDING, COMPLETED;


    public static PaymentEnum fromString(String status, PaymentEnum defaultValue)
    {
        if (status == null)
        {
            return defaultValue;
        }
        try
        {
            return PaymentEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e)
        {
            return defaultValue;
        }
    }
}
