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
    @Column(nullable = false)
    private Integer invoiced_orders_count;
    @Column(nullable = false)
    private Integer invoiced_items_count;
    @Column(nullable = false)
    private Double total_revenue;
}
