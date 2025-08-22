package com.silverwing.dorothy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SaleStatDto {
    String period;
    long totalCount;
    long totalSale;
    long manCount;
    long manSale;
    long womanCount;
    long womanSale;
/*
    public SaleStatDto(String period, long totalCount, BigDecimal totalSale, long manCutCount, BigDecimal manCutSale, long manRootCount, BigDecimal manRootSale, long manPermCount, BigDecimal manPermSale, long womanCutCount, BigDecimal womanCutSale, long womanRootCount, BigDecimal womanRootSale) {
        this.period = period;
        this.totalCount = totalCount;
        this.totalSale = totalSale.longValue();
        this.manCutCount = manCutCount;
        this.manCutSale = manCutSale.longValue();
        this.manRootCount = manRootCount;
        this.manRootSale = manRootSale.longValue();
        this.manPermCount = manPermCount;
        this.manPermSale = manPermSale.longValue();
        this.womanCutCount = womanCutCount;
        this.womanCutSale = womanCutSale.longValue();
        this.womanRootCount = womanRootCount;
        this.womanRootSale = womanRootSale.longValue();
    }
*/
    public SaleStatDto(String period, long totalCount, double totalSale, long manCount, double manSale,long womanCount, double womanSale) {
        this.period = period;
        this.totalCount = totalCount;
        this.totalSale = (long) totalSale;
        this.manCount = manCount;
        this.manSale = (long)manSale;
        this.womanCount = womanCount;
        this.womanSale = (long)womanSale;
    }
}
