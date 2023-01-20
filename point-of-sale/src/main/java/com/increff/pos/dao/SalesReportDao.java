package com.increff.pos.dao;

import com.increff.pos.pojo.DailySalesReportPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;


@Repository
public class SalesReportDao extends AbstractDao {

    private String select_all = "select p from DailySalesReportPojo p";
    private String select_between = "select p from DailySalesReportPojo p where p.date between :startingDate and :endingDate";

    public void insert(DailySalesReportPojo salesReportPojo) {
        em.persist(salesReportPojo);
    }

    public List<DailySalesReportPojo> selectAll() {
        TypedQuery<DailySalesReportPojo> query = getQuery(select_all, DailySalesReportPojo.class);
        return query.getResultList();
    }

    public List<DailySalesReportPojo> selectAllBetween(Date startingDate, Date endingDate) {
        TypedQuery<DailySalesReportPojo> query = getQuery(select_between, DailySalesReportPojo.class);
        query.setParameter("startingDate", startingDate);
        query.setParameter("endingDate", endingDate);
        return query.getResultList();
    }
}
