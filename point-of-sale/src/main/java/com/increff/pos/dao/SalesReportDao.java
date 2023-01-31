package com.increff.pos.dao;

import com.increff.pos.pojo.DailySalesReportPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;


@Repository
public class SalesReportDao extends AbstractDao {

    private String select_all = "select p from DailySalesReportPojo p";

    public void insert(DailySalesReportPojo salesReportPojo) {
        em.persist(salesReportPojo);
    }

    public List<DailySalesReportPojo> selectAll() {
        TypedQuery<DailySalesReportPojo> query = getQuery(select_all, DailySalesReportPojo.class);
        return query.getResultList();
    }
}
