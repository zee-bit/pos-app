package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "daily_sales_report",
indexes = {
        @Index(name = "date_idx", columnList = "date")
})
public class DailySalesReportPojo {

    @Id
    private Date date;
    @Column(name = "invoiced_orders_count", nullable = false)
    private Integer invoicedOrdersCount;
    @Column(name = "invoiced_items_count", nullable = false)
    private Integer invoicedItemsCount;
    @Column(name = "total_revenue", nullable = false)
    private Double totalRevenue;
}
