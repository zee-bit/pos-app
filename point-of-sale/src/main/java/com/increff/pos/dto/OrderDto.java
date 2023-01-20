package com.increff.pos.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderDetailData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.FileUtil;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.ConversionUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderDto {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private RestTemplate restTemplate;

    final String INVOICE_APP_URL = "http://localhost:8000/invoice/";

    @Transactional(rollbackFor = ApiException.class)
    public OrderDetailData addOrder(List<OrderItemForm> orderItemForms) throws ApiException {
        validateFields(orderItemForms);
        normalize(orderItemForms);
        OrderPojo orderPojo = orderService.createNewOrder();
        List<OrderItemPojo> orderItemPojoList = new ArrayList<OrderItemPojo>();
        List<OrderItemData> orderItemDataList = new ArrayList<OrderItemData>();
        for (OrderItemForm orderItemForm : orderItemForms) {
            ProductPojo productPojo = productService.getByBarcode(orderItemForm.getBarcode());
            OrderItemPojo orderItemPojo = ConversionUtil.getOrderItemPojo(orderItemForm, orderPojo.getId(), productPojo.getId());
            OrderItemData orderItemData = ConversionUtil.getOrderItemData(orderItemPojo, productPojo);
            orderItemPojoList.add(orderItemPojo);
            orderItemDataList.add(orderItemData);
            orderItemService.insert(orderItemPojo);
            inventoryService.reduce(orderItemForm.getBarcode(), orderItemPojo.getProductId(), orderItemPojo.getQuantity());
        }

        OrderData orderData = ConversionUtil.getOrderData(orderPojo, orderItemPojoList);
        OrderDetailData orderDetailData = ConversionUtil.getOrderDetailsData(orderData, orderItemDataList);
        generateInvoice(orderPojo.getId());
        return orderDetailData;
    }

    public OrderData get(Integer id) throws ApiException {
        OrderPojo orderPojo = orderService.getById(id);
        List<OrderItemPojo> orderItemPojos = orderItemService.get(orderPojo.getId());
        OrderData orderData = ConversionUtil.getOrderData(orderPojo, orderItemPojos);
        return orderData;
    }

    public OrderDetailData getOrderDetails(int orderId) throws ApiException {
        OrderPojo orderPojo = orderService.getById(orderId);
        List<OrderItemPojo> orderItemPojo = orderItemService.get(orderId);
        OrderData orderData = ConversionUtil.getOrderData(orderPojo, orderItemPojo);
        List<OrderItemData> orderItemDatas = new ArrayList<OrderItemData>();
        for (OrderItemPojo orderItem : orderItemPojo) {
            ProductPojo product = productService.get(orderItem.getProductId());
            OrderItemData orderItemData = ConversionUtil.getOrderItemData(orderItem, product);
            orderItemDatas.add(orderItemData);
        }
        return ConversionUtil.getOrderDetailsData(orderData, orderItemDatas);
    }

    public List<OrderData> getAllOrders() throws ApiException {
        List<OrderPojo> orderPojos = orderService.getAll();
        List<OrderData> orderDatas = new ArrayList<OrderData>();
        for (OrderPojo orderPojo : orderPojos) {
            List<OrderItemPojo> orderItemPojos = orderItemService.get(orderPojo.getId());
            OrderData orderData = ConversionUtil.getOrderData(orderPojo, orderItemPojos);
            orderDatas.add(orderData);
        }
        return orderDatas;
    }

    @Transactional(rollbackFor = ApiException.class)
    public List<OrderItemPojo> updateOrder(Integer orderId, List<OrderItemForm> orderItemForms) throws ApiException {
        validateFields(orderItemForms);
        normalize(orderItemForms);
        revertInventory(orderId);
        OrderPojo orderPojo = orderService.getById(orderId);
        List<OrderItemPojo> orderItemPojos = new ArrayList<>();
        for (OrderItemForm orderItemForm : orderItemForms) {
            ProductPojo productPojo = productService.getByBarcode(orderItemForm.getBarcode());

            OrderItemPojo orderItemPojo = ConversionUtil.getOrderItemPojo(orderItemForm, orderPojo.getId(), productPojo.getId());
            orderItemPojos.add(orderItemPojo);
            inventoryService.reduce(orderItemForm.getBarcode(), orderItemPojo.getProductId(), orderItemPojo.getQuantity());
        }
        orderItemService.deleteByOrderId(orderId);
        orderItemService.insertMultiple(orderItemPojos);
        generateInvoice(orderPojo.getId());
        return orderItemPojos;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void revertInventory(int orderId) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemService.get(orderId);
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            inventoryService.increase(orderItemPojo.getProductId(), orderItemPojo.getQuantity());
        }
    }

    public void validateFields(List<OrderItemForm> orderItems) throws ApiException {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new ApiException("Order items cannot be empty");
        }
        for (OrderItemForm orderItem : orderItems) {
            if (orderItem.getQuantity() <= 0) {
                throw new ApiException("Quantity cannot be less than or equal to 0");
            }
            if (orderItem.getSellingPrice() < 0) {
                throw new ApiException("Selling Price cannot be less than 0");
            }
        }
    }

    public void normalize(List<OrderItemForm> orderItemFormList) {
        for (OrderItemForm orderItemForm : orderItemFormList) {
            orderItemForm.setBarcode(StringUtil.toLowerCase(orderItemForm.getBarcode()));
        }
    }

    public void generateInvoice(Integer orderId) throws ApiException {
        OrderDetailData orderDetailData = getOrderDetails(orderId);
        String base64Str = restTemplate.postForObject(INVOICE_APP_URL, orderDetailData, String.class);

        String baseDir = "/home/zean/pos/";
        File bill = new File(baseDir + "invoiceFiles/bill" + orderId.toString() + ".pdf");

        byte[] decodedPdf = Base64.decodeBase64(base64Str);
        try {
            FileOutputStream outputStream = new FileOutputStream(bill);
            outputStream.write(decodedPdf);
            outputStream.flush();
            outputStream.getFD().sync();
            outputStream.close();
        }
        catch (IOException e) {
            throw new ApiException("Order cannot be placed due to server issues. Please try again");
        }
    }

    public void updateInvoiceStatus(Integer id) throws ApiException {
        orderService.setInvoiceDownloaded(id);
    }
}
