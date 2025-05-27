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
    long manCutCount;
    long manCutSale;
    long manRootCount;
    long manRootSale;
    long manPermCount;
    long manPermSale;
    long womanCutCount;
    long womanCutSale;
    long womanRootCount;
    long womanRootSale;
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
    public SaleStatDto(String period, long totalCount, double totalSale, long manCutCount, double manCutSale, long manRootCount, double manRootSale, long manPermCount, double manPermSale, long womanCutCount, double womanCutSale, long womanRootCount, double womanRootSale) {
        this.period = period;
        this.totalCount = totalCount;
        this.totalSale = (long) totalSale;
        this.manCutCount = manCutCount;
        this.manCutSale = (long)manCutSale;
        this.manRootCount = manRootCount;
        this.manRootSale = (long)manRootSale;
        this.manPermCount = manPermCount;
        this.manPermSale = (long)manPermSale;
        this.womanCutCount = womanCutCount;
        this.womanCutSale = (long)womanCutSale;
        this.womanRootCount = womanRootCount;
        this.womanRootSale = (long)womanRootSale;
    }
}
