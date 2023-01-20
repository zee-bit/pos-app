package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao {

    private String select_all = "select o from OrderPojo o";
    private String select_between = "select o from OrderPojo o where o.createdAt between :startingDate and :endingDate";

    public OrderPojo insert(OrderPojo p) {
        em.persist(p);
        return p;
    }

    public OrderPojo select(Integer id) {
        return em.find(OrderPojo.class, id);
    }

    public List<OrderPojo> selectAll() {
        TypedQuery<OrderPojo> query = getQuery(select_all, OrderPojo.class);
        return query.getResultList();
    }

    public OrderPojo update(OrderPojo p) {
        em.merge(p);
        return p;
    }

    public List<OrderPojo> selectAllBetween(Date startingDate, Date endingDate) {
        TypedQuery<OrderPojo> query = getQuery(select_between, OrderPojo.class);
        query.setParameter("startingDate", startingDate);
        query.setParameter("endingDate", endingDate);
        return query.getResultList();
    }
}
