package com.increff.pos.utils;

import com.increff.pos.model.form.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static BrandForm getBrandForm(String brand, String category) {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(brand);
        brandForm.setCategory(category);
        return brandForm;
    }

    public static BrandPojo getBrandPojo(String brand, String category) {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(brand);
        brandPojo.setCategory(category);
        return brandPojo;
    }

    public static ProductForm getProductForm(String name, String barcode, String brand, String category, Double price) {
        ProductForm productForm = new ProductForm();
        productForm.setProduct(name);
        productForm.setBarcode(barcode);
        productForm.setBrandName(brand);
        productForm.setBrandCategory(category);
        productForm.setPrice(price);
        return productForm;
    }

    public static ProductPojo getProductPojo(String name, String barcode, Integer brandId, Double price) {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setProduct(name);
        productPojo.setBarcode(barcode);
        productPojo.setBrandId(brandId);
        productPojo.setPrice(price);
        return productPojo;
    }

    public static InventoryForm getInventoryForm(String barcode, Integer quantity) {
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }

    public static InventoryPojo getInventoryPojo(Integer productId, Integer quantity) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productId);
        inventoryPojo.setQuantity(quantity);
        return inventoryPojo;
    }

    public static OrderItemForm getOrderItemForm(String barcode, Integer quantity, Double sellingPrice) {
        OrderItemForm orderItemForm = new OrderItemForm();
        orderItemForm.setBarcode(barcode);
        orderItemForm.setQuantity(quantity);
        orderItemForm.setSellingPrice(sellingPrice);
        return orderItemForm;
    }

    public static UserForm getUserForm(String email, String password) {
        UserForm userForm = new UserForm();
        userForm.setEmail(email);
        userForm.setPassword(password);
        return userForm;
    }

    public static SalesReportForm getSalesReportForm(String brand, String category) {
        SalesReportForm salesReportForm = new SalesReportForm();
        salesReportForm.setBrand(brand);
        salesReportForm.setCategory(category);
        return salesReportForm;
    }

    public static List<OrderItemForm> getOrderItemForm(
            List<String> barcodes, List<Integer> quantities, List<Double> sellingPriceList) {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        for(int i = 0; i < barcodes.size(); i++) {
            OrderItemForm orderItemForm = new OrderItemForm();
            orderItemForm.setBarcode(barcodes.get(i));
            orderItemForm.setQuantity(quantities.get(i));
            orderItemForm.setSellingPrice(sellingPriceList.get(i));
            orderItemFormList.add(orderItemForm);
        }
        return orderItemFormList;
    }

    public static List<OrderItemPojo> getOrderItemPojoList(
            Integer orderId, List<ProductPojo> productPojoList, List<Integer> quantities, List<Double> sellingPriceList) {
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        for(int i = 0; i < productPojoList.size(); i++) {
            OrderItemPojo orderItemPojo = new OrderItemPojo();
            orderItemPojo.setOrderId(orderId);
            orderItemPojo.setProductId(productPojoList.get(i).getId());
            orderItemPojo.setQuantity(quantities.get(i));
            orderItemPojo.setSellingPrice(sellingPriceList.get(i));
            orderItemPojoList.add(orderItemPojo);
        }
        return orderItemPojoList;
    }
}
