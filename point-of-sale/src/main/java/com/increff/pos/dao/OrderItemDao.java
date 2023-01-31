package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao {

    private String select_id = "select oi from OrderItemPojo oi where orderId=:orderId";

    @Transactional
    public void insert(OrderItemPojo orderItemPojo) {
        em.persist(orderItemPojo);
    }

    public List<OrderItemPojo> selectByOrderId(Integer orderId) {
        TypedQuery<OrderItemPojo> query = getQuery(select_id, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }

    @Transactional
    public void deleteByOrderId(Integer orderId) {
        TypedQuery<OrderItemPojo> query = getQuery("select p from OrderItemPojo p where orderId=:orderId",
                OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        List<OrderItemPojo> list = query.getResultList();
        for (OrderItemPojo p : list) {
            em.remove(p);
        }
    }
}
