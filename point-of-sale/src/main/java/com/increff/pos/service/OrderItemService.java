package com.increff.pos.service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemDao dao;
    @Autowired
    private OrderService orderService;

    public List<OrderItemPojo> get(Integer orderId) throws ApiException {
        return dao.selectByOrderId(orderId);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void deleteByOrderId(Integer orderId) {
        dao.deleteByOrderId(orderId);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void insertMultiple(List<OrderItemPojo> orderItemPojos) {
        for (OrderItemPojo orderItemPojo : orderItemPojos) {
            dao.insert(orderItemPojo);
        }
    }

    public List<OrderItemPojo> getAllOrderItemBetween(Date startingDate, Date endingDate) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = new ArrayList<OrderItemPojo>();
        List<OrderPojo> orderPojoList = orderService.getAllBetween(startingDate, endingDate);
        for(OrderPojo orderPojo : orderPojoList) {
            List<OrderItemPojo> orderItemPojo = get(orderPojo.getId());
            orderItemPojoList.addAll(orderItemPojo);
        }
        return orderItemPojoList;
    }

    public void insert(OrderItemPojo orderItemPojo) {
        dao.insert(orderItemPojo);
    }
}
