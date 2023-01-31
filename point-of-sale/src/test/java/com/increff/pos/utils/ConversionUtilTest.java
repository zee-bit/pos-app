package com.increff.pos.utils;

import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.model.data.*;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.*;
import com.increff.pos.util.ConversionUtil;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConversionUtilTest {

    @Test
    public void getBrandDataTest() {
        BrandPojo brandPojo = TestUtils.getBrandPojo(" apple ", " Electronics ");
        brandPojo.setId(1);
        BrandData brandData = ConversionUtil.getBrandData(brandPojo);
        assertEquals(brandPojo.getBrand(), brandData.getBrand());
        assertEquals(brandPojo.getCategory(), brandData.getCategory());
        assertEquals(brandPojo.getId(), brandData.getId());
    }

    @Test
    public void getBrandPojoTest() {
        BrandForm brandForm = TestUtils.getBrandForm(" apple ", " Electronics ");
        BrandPojo brandPojo = ConversionUtil.getBrandPojo(brandForm);
        assertEquals(brandForm.getBrand(), brandPojo.getBrand());
        assertEquals(brandForm.getCategory(), brandPojo.getCategory());
    }

    @Test
    public void getProductPojoTest() {
        ProductForm productForm = TestUtils.getProductForm("milky bar","da123","amul","dairy", 10.0);
        ProductPojo productPojo = ConversionUtil.getProductPojo(productForm,1);
        assertEquals(productForm.getProduct(), productPojo.getProduct());
        assertEquals(productForm.getPrice(), productPojo.getPrice());
        assertEquals(productForm.getBarcode(), productPojo.getBarcode());
        assertEquals((Integer) 1, productPojo.getBrandId());
    }

    @Test
    public void getProductDataTest() {
        ProductPojo productPojo = TestUtils.getProductPojo("iphone","ipx123",1,79999.99);
        ProductData productData = ConversionUtil.getProductData(productPojo, "apple", "electronics");
        assertEquals(productPojo.getProduct(), productData.getProduct());
        assertEquals(productPojo.getBarcode(), productData.getBarcode());
        assertEquals(productPojo.getPrice(), productData.getPrice());
        assertEquals("apple", productData.getBrandName());
        assertEquals("electronics", productData.getBrandCategory());
    }

    @Test
    public void getInventoryDataTest() {
        InventoryPojo inventoryPojo = TestUtils.getInventoryPojo(1,50);
        ProductPojo productPojo = TestUtils.getProductPojo("iphone","ipx123",1,79999.99);
        InventoryData inventoryData = ConversionUtil.getInventoryData(inventoryPojo, "iphone", "ipx123");
        assertEquals(productPojo.getProduct(), inventoryData.getProduct());
        assertEquals(productPojo.getBarcode(), inventoryData.getBarcode());
        assertEquals(inventoryPojo.getQuantity(), inventoryData.getQuantity());
    }

    @Test
    public void getInventoryPojoTest() {
        InventoryForm inventoryForm = TestUtils.getInventoryForm("ipx123",50);
        InventoryPojo inventoryPojo = ConversionUtil.getInventoryPojo(inventoryForm, 1);
        assertEquals((Integer) 1, inventoryPojo.getProductId());
        assertEquals(inventoryForm.getQuantity(), inventoryPojo.getQuantity());
    }

    @Test
    public void getOrderItemDataTest() {
        ProductPojo productPojo = TestUtils.getProductPojo("iphone","ipx123",1,79999.99);
        List<ProductPojo> products = Arrays.asList(productPojo);
        List<Integer>quantities = Arrays.asList(6);
        List<Double>sellingPrices = Arrays.asList(52.0);
        List<OrderItemPojo> orderItemPojos = TestUtils.getOrderItemPojoList(1,products,quantities,sellingPrices);
        OrderItemData orderItemData = ConversionUtil.getOrderItemData(orderItemPojos.get(0), productPojo);
        assertEquals(orderItemPojos.get(0).getQuantity(), orderItemData.getQuantity());
        assertEquals(orderItemPojos.get(0).getSellingPrice(), orderItemData.getSellingPrice());
        assertEquals("ipx123", orderItemData.getBarcode());
        assertEquals("iphone", orderItemData.getProductName());
    }

    @Test
    public void getOrderItemPojoTest() {
        List<String>barcodes = Arrays.asList("am111");
        List<Integer>quantities = Arrays.asList(6);
        List<Double>sellingPrices = Arrays.asList(52.0);
        List<OrderItemForm> orderItemFormList = TestUtils.getOrderItemForm(barcodes,quantities,sellingPrices);
        OrderItemPojo orderItemPojo = ConversionUtil.getOrderItemPojo(orderItemFormList.get(0),1,1);
        assertEquals(orderItemFormList.get(0).getQuantity(), orderItemPojo.getQuantity());
        assertEquals(orderItemFormList.get(0).getSellingPrice(), orderItemPojo.getSellingPrice());
        assertEquals((Integer) 1, orderItemPojo.getOrderId());
        assertEquals((Integer) 1,orderItemPojo.getProductId());
    }

    @Test
    public void getOrderDetailsDataTest() {
        List<String> barcodes = Arrays.asList("am111");
        List<Integer> quantities = Arrays.asList(6);
        List<Double> sellingPrices = Arrays.asList(52.0);
        List<Integer> orderIds = Arrays.asList(1);
        List<String> productNames = Arrays.asList("milky bar");
        List<OrderItemData> orderItemDataList = TestUtils.getOrderItemDataList(orderIds, productNames, barcodes, quantities, sellingPrices);
        OrderData orderData = TestUtils.getOrderData(1, new Timestamp(1), 52.0);
        OrderDetailData orderDetailData = ConversionUtil.getOrderDetailsData(orderData, orderItemDataList);
        assertEquals(orderItemDataList, orderDetailData.getOrderItems());
        assertEquals((Double)52.0, orderDetailData.getBillAmount());
        assertEquals(new Timestamp(1), orderDetailData.getCreatedAt());
    }

    @Test
    public void setDailySalesReportDataTest() {
        DailySalesReportPojo dailySalesReportPojo = TestUtils.getDailySalesReportPojo(new Date(),10.0,1,1);
        DailySalesReportData dailySalesReportData = ConversionUtil.getDailySalesReportData(dailySalesReportPojo);
        assertEquals(dailySalesReportPojo.getDate().getClass(),Date.class );
        assertEquals(dailySalesReportPojo.getInvoicedItemsCount(), dailySalesReportData.getItemCount());
        assertEquals(dailySalesReportPojo.getInvoicedOrdersCount(),dailySalesReportData.getOrderCount());
        assertEquals(dailySalesReportPojo.getTotalRevenue(), dailySalesReportData.getTotalRevenue());
    }

    @Test
    public void convertFormToUserPojo() {
        UserForm userForm = TestUtils.getUserForm("xyz@xyz.com","xyz", "xyz");
        UserPojo pojo = ConversionUtil.getUserPojoFromForm(userForm);
        assertEquals(userForm.getEmail(), pojo.getEmail());
        assertEquals(userForm.getPassword(), pojo.getPassword());
    }

    @Test
    public void convertPojoToUserData() {
        UserPojo userPojo = new UserPojo();
        userPojo.setEmail("xyz@xyz.com");
        userPojo.setId(1);
        userPojo.setRole("supervisor");
        UserData userData = ConversionUtil.getUserDataFromPojo(userPojo);
        assertEquals(userPojo.getEmail(), userData.getEmail());
        assertEquals(userPojo.getRole(), userData.getRole());
        assertEquals(1, userData.getId());
    }
}
