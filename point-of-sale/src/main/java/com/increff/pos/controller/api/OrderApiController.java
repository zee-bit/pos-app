package com.increff.pos.controller.api;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderDetailData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class OrderApiController {

    @Autowired
    private OrderDto dto;

    @ApiOperation(value = "Create a new Order")
    @RequestMapping(path = "/api/orders", method = RequestMethod.POST)
    public OrderDetailData add(@RequestBody List<OrderItemForm> orderItems) throws ApiException {
        return dto.addOrder(orderItems);
    }

    @ApiOperation(value = "Gets Order details by order id")
    @RequestMapping(path = "/api/orders/{id}", method = RequestMethod.GET)
    public OrderDetailData getOrderDetails(@PathVariable Integer id) throws ApiException {
        return dto.getOrderDetails(id);
    }

    @ApiOperation(value = "Gets list of all Orders")
    @RequestMapping(path = "/api/orders", method = RequestMethod.GET)
    public List<OrderData> getAll() throws ApiException {
        return dto.getAllOrders();
    }

    @ApiOperation(value = "Updates an Order")
    @RequestMapping(path = "/api/orders/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable Integer id, @RequestBody List<OrderItemForm> orderItems) throws ApiException {
        dto.updateOrder(id, orderItems);
    }

    @ApiOperation(value = "Updates invoice download status")
    @RequestMapping(path = "/api/orders/invoice/{id}", method = RequestMethod.GET)
    public void invoiceDownloaded(@PathVariable Integer id) throws ApiException {
        dto.updateInvoiceStatus(id);
    }
}
