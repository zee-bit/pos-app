package com.increff.pos.util;

import com.increff.pos.model.data.*;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.*;

import java.util.List;

public class ConversionUtil {

    // BRANDS
    // BrandPOJO -> BrandData
    public static BrandData getBrandData(BrandPojo p) {
        BrandData data = new BrandData();
        data.setId(p.getId());
        data.setBrand(p.getBrand());
        data.setCategory(p.getCategory());
        return data;
    }

    // BrandForm -> BrandPOJO
    public static BrandPojo getBrandPojo(BrandForm f) {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(f.getBrand());
        brandPojo.setCategory(f.getCategory());
        return brandPojo;
    }

    // PRODUCTS
    // ProductPOJO -> ProductData
    public static ProductData getProductData(ProductPojo p, String brandName, String brandCategory) {
        ProductData data = new ProductData();
        data.setId(p.getId());
        data.setProduct(p.getProduct());
        data.setPrice(p.getPrice());
        data.setBarcode(p.getBarcode());
        data.setBrandName(brandName);
        data.setBrandCategory(brandCategory);
        return data;
    }

    // ProductForm -> ProductPOJO
    public static ProductPojo getProductPojo(ProductForm f, Integer brandId) {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setProduct(f.getProduct());
        productPojo.setPrice(f.getPrice());
        productPojo.setBarcode(f.getBarcode());
        productPojo.setBrandId(brandId);
        return productPojo;
    }

    // INVENTORY
    // InventoryPOJO -> InventoryData
    public static InventoryData getInventoryData(InventoryPojo i, String name, String barcode) {
        InventoryData data = new InventoryData();
        data.setProduct(name);
        data.setBarcode(barcode);
        data.setQuantity(i.getQuantity());
        return data;
    }

    // InventoryForm -> InventoryPOJO
    public static InventoryPojo getInventoryPojo(InventoryForm f, Integer productId) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productId);
        inventoryPojo.setQuantity(f.getQuantity());
        return inventoryPojo;
    }

    // ORDER ITEMS
    // OrderItemPOJO -> OrderItemData
    public static OrderItemData getOrderItemData(OrderItemPojo o, ProductPojo p) {
        OrderItemData data = new OrderItemData();
        data.setId(o.getId());
        data.setOrderId(o.getOrderId());
        data.setBarcode(p.getBarcode());
        data.setQuantity(o.getQuantity());
        data.setProductName(p.getProduct());
        data.setSellingPrice(o.getSellingPrice());
        return data;
    }

    // OrderItemForm -> OrderItemPOJO
    public static OrderItemPojo getOrderItemPojo(OrderItemForm f, Integer orderId, Integer productId) {
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setOrderId(orderId);
        orderItemPojo.setProductId(productId);
        orderItemPojo.setQuantity(f.getQuantity());
        orderItemPojo.setSellingPrice(f.getSellingPrice());
        return orderItemPojo;
    }

    // ORDERS
    // OrderPOJO -> OrderData
    public static OrderData getOrderData(OrderPojo p, List<OrderItemPojo> orderItems) {
        OrderData orderData = new OrderData();
        orderData.setId(p.getId());
        orderData.setCreatedAt(p.getCreatedAt());

        double billAmount = 0;
        for(OrderItemPojo orderItem : orderItems) {
            billAmount += orderItem.getQuantity() * orderItem.getSellingPrice();
        }

        orderData.setIsInvoiceCreated(p.getIsInvoiceCreated());
        orderData.setBillAmount(billAmount);
        return orderData;
    }

    public static OrderDetailData getOrderDetailsData(OrderData orderData, List<OrderItemData> orderItems) {
        OrderDetailData orderDetailData = new OrderDetailData();
        orderDetailData.setOrderItems(orderItems);
        orderDetailData.setId(orderData.getId());
        orderDetailData.setCreatedAt(orderData.getCreatedAt());
        orderDetailData.setBillAmount(orderData.getBillAmount());
        orderDetailData.setIsInvoiceCreated(orderData.getIsInvoiceCreated());
        return orderDetailData;
    }

    public static DailySalesReportData getDailySalesReportData(DailySalesReportPojo dailySalesReportPojo) {
        DailySalesReportData dailySalesReportData = new DailySalesReportData();
        dailySalesReportData.setDate(dailySalesReportPojo.getDate());
        dailySalesReportData.setOrderCount(dailySalesReportPojo.getInvoicedOrdersCount());
        dailySalesReportData.setItemCount(dailySalesReportPojo.getInvoicedItemsCount());
        dailySalesReportData.setTotalRevenue(dailySalesReportPojo.getTotalRevenue());
        return dailySalesReportData;
    }

    // USERS
    // UserForm -> UserPojo
    public static UserPojo getUserPojoFromForm(UserForm userForm) {
        UserPojo user = new UserPojo();
        user.setEmail(userForm.getEmail());
        user.setRole(userForm.getRole());
        user.setPassword(userForm.getPassword());
        return user;
    }

    public static UserData getUserDataFromPojo(UserPojo user) {
        UserData data = new UserData();
        data.setEmail(user.getEmail());
        data.setRole(user.getRole());
        data.setId(user.getId());
        return data;
    }

    public static BrandForm getBrandFormFromProductSearchForm(ProductSearchForm form) {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(form.getBrand());
        brandForm.setCategory(form.getCategory());
        return brandForm;
    }
}
