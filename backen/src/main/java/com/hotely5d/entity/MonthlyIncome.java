package com.hotely5d.entity;

import java.math.BigDecimal;

public class MonthlyIncome {
    private String month;
    private BigDecimal income;

    public MonthlyIncome(String month, BigDecimal income) {
        this.month = month;
        this.income = income;
    }


    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
