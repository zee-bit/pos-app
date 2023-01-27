package com.increff.pos.dto;

import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderDetailData;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.utils.TestUtils;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderDtoTest extends AbstractUnitTest {

    @Autowired
    private BrandDto brandDto;
    @Autowired
    private ProductDto productDto;
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private OrderDto orderDto;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void init() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("Nike ", "Shoes");
        brandDto.add(brandForm);
        ProductForm productForm = TestUtils.getProductForm(
                "Runner", "NK123", " Nike", "Shoes ", 13999.00);
        productDto.add(productForm);
        ProductForm newProductForm = TestUtils.getProductForm(
                "Flyer", "NK321", "Nike", "Shoes", 10999.99);
        productDto.add(newProductForm);
        InventoryForm inventoryForm = TestUtils.getInventoryForm("NK123", 50);
        inventoryDto.update(inventoryForm);
        InventoryForm newInventoryForm = TestUtils.getInventoryForm("NK321", 30);
        inventoryDto.update(newInventoryForm);
    }

    @Test
    public void addOrderTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123", 1, 13999.00);
        orderItemFormList.add(orderItemForm);
        orderItemForm = TestUtils.getOrderItemForm("NK321", 2, 10999.00);
        orderItemFormList.add(orderItemForm);
        OrderDetailData orderDetailData = orderDto.addOrder(orderItemFormList);
        assertEquals((Double)35997.0, orderDetailData.getBillAmount());
        assertEquals(2, orderDetailData.getOrderItems().size());
        assertEquals((Integer)1, orderDetailData.getOrderItems().get(0).getQuantity());
        assertEquals((Double)13999.00, orderDetailData.getOrderItems().get(0).getSellingPrice());
        assertEquals((Integer)2, orderDetailData.getOrderItems().get(1).getQuantity());
        assertEquals((Double)10999.00, orderDetailData.getOrderItems().get(1).getSellingPrice());
    }

    @Test
    public void addOrderWithExcessQuantityTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123", 52, 13999.00);
        orderItemFormList.add(orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Not enough quantity available for product, barcode:nk123");
        orderDto.addOrder(orderItemFormList);
    }

    @Test
    public void addNonExistingBarcodeTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("KN789", 1, 13999.00);
        orderItemFormList.add(orderItemForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with barcode kn789 does not exists");
        orderDto.addOrder(orderItemFormList);
    }

    @Test
    public void getAllOrdersTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123", 1, 13999.00);
        orderItemFormList.add(orderItemForm);
        orderItemForm = TestUtils.getOrderItemForm("NK321", 2, 10999.00);
        orderItemFormList.add(orderItemForm);
        orderDto.addOrder(orderItemFormList);
        List<OrderData> data = orderDto.getAllOrders();
        assertEquals(1, data.size());
    }

    @Test
    public void getOrderByIdTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123", 1, 13999.00);
        orderItemFormList.add(orderItemForm);
        OrderDetailData orderData = orderDto.addOrder(orderItemFormList);
        OrderData data = orderDto.get(orderData.getId());
        assertEquals((Double)13999.0, orderData.getBillAmount());
        assertEquals(1, orderData.getOrderItems().size());
        assertEquals((Integer)1, orderData.getOrderItems().get(0).getQuantity());
    }

    @Test
    public void getOrderDetailsTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123", 1, 13999.00);
        orderItemFormList.add(orderItemForm);
        OrderDetailData orderData = orderDto.addOrder(orderItemFormList);
        OrderDetailData orderDetailData = orderDto.getOrderDetails(orderData.getId());
        assertEquals((Double)13999.0, orderDetailData.getBillAmount());
        assertEquals(1, orderDetailData.getOrderItems().size());
        assertEquals((Integer)1, orderDetailData.getOrderItems().get(0).getQuantity());
        assertEquals((Double)13999.00, orderDetailData.getOrderItems().get(0).getSellingPrice());
    }

    @Test
    public void updateOrderTest() throws ApiException {
        // Add initial order
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123", 4, 13999.00);
        orderItemFormList.add(orderItemForm);
        OrderDetailData orderDetailData = orderDto.addOrder(orderItemFormList);

        // Update order
        List<OrderItemForm> updatedOrderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm updatedOrderItemForm = TestUtils.getOrderItemForm("NK123", 5, 13999.00);
        updatedOrderItemFormList.add(updatedOrderItemForm);
        OrderItemForm newOrderItemForm = TestUtils.getOrderItemForm("NK321", 5, 10999.00);
        updatedOrderItemFormList.add(newOrderItemForm);
        List<OrderItemPojo> updatedOrderItemPojoList = orderDto.updateOrder(orderDetailData.getId(), updatedOrderItemFormList);

        // Assert
        assertEquals(2, updatedOrderItemPojoList.size());
        assertEquals((Integer)5, updatedOrderItemPojoList.get(0).getQuantity());
        assertEquals((Double)13999.00, updatedOrderItemPojoList.get(0).getSellingPrice());
        assertEquals((Integer)5, updatedOrderItemPojoList.get(1).getQuantity());
        assertEquals((Double)10999.00, updatedOrderItemPojoList.get(1).getSellingPrice());
    }

    @Test
    public void updateInvoiceStatusTest() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<OrderItemForm>();
        OrderItemForm orderItemForm = TestUtils.getOrderItemForm("NK123", 1, 13999.00);
        orderItemFormList.add(orderItemForm);
        orderItemForm = TestUtils.getOrderItemForm("NK321", 2, 10999.00);
        orderItemFormList.add(orderItemForm);
        OrderDetailData orderDetailData = orderDto.addOrder(orderItemFormList);
        orderDto.updateInvoiceStatus(orderDetailData.getId());
        OrderData orderData = orderDto.get(orderDetailData.getId());
        assertEquals(true, orderData.getIsInvoiceCreated());
    }
}
