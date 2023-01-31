package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo createNewOrder() {
        OrderPojo orderPojo = new OrderPojo();
        return dao.insert(orderPojo);
    }

    public OrderPojo getById(Integer id) throws ApiException {
        OrderPojo orderPojo = dao.select(id);
        if (orderPojo == null) {
            throw new ApiException("Order with given id not found");
        }
        return orderPojo;
    }

    public List<OrderPojo> getAll() {
        return dao.selectAll();
    }

    public List<OrderPojo> getAllBetween(Date startingDate, Date endingDate) {
        return dao.selectAllBetween(startingDate, endingDate);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void setInvoiceDownloaded(Integer id) throws ApiException {
        OrderPojo orderPojo = getById(id);
        orderPojo.setIsInvoiceCreated(true);
    }
}
