package com.increff.pos.dto;

import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.DailySalesReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReportDtoTest extends AbstractUnitTest {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ReportDto reportDto;


    @Before
    public void init() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("nike", "shoes");
        brandService.add(brandPojo);
        ProductPojo productPojo = TestUtils.getProductPojo("runner", "nk123", brandPojo.getId(), 13999d);
        productService.add(productPojo);
        ProductPojo newProductPojo = TestUtils.getProductPojo("flyer", "nk321", brandPojo.getId(), 10999d);
        productService.add(newProductPojo);
        InventoryPojo inventoryPojo = TestUtils.getInventoryPojo(productPojo.getId(), 0);
        inventoryService.add(inventoryPojo);
        InventoryPojo newInventoryPojo = TestUtils.getInventoryPojo(newProductPojo.getId(), 0);
        inventoryService.add(newInventoryPojo);
        OrderPojo orderPojo = orderService.createNewOrder();
        List<ProductPojo>products = new ArrayList<>();
        products.add(productPojo);
        List<Integer>quantities = new ArrayList<>();
        quantities.add(6);
        List<Double>sellingPrices = new ArrayList<>();
        sellingPrices.add(52.0);
        List<OrderItemPojo> orderItemPojos = TestUtils.getOrderItemPojoList(orderPojo.getId(),products,quantities,sellingPrices);
        orderItemService.insertMultiple(orderItemPojos);
    }


    @Test
    public void getInventoryReportTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("nike", "shoes");
        List<InventoryReportData> list = reportDto.getInventoryReport(brandForm);
        assertEquals(1,list.size());
    }

    @Test
    public void getBrandCategoryReportTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("nike", "shoes");
        List<BrandData> list = reportDto.getBrandCategoryReport(brandForm);
        assertEquals(1,list.size());
    }

    @Test
    public void getSalesReportEmptyBrandCategoryTest() throws ApiException {
        SalesReportForm salesReportForm = TestUtils.getSalesReportForm("","");
        List<SalesReportData> list = reportDto.getSalesReport(salesReportForm);
        assertEquals(1,list.size());
    }

    @Test
    public void getSalesReportEmptyBrandTest() throws ApiException {
        SalesReportForm salesReportForm = TestUtils.getSalesReportForm("","shoes");
        List<SalesReportData> list = reportDto.getSalesReport(salesReportForm);
        assertEquals(1,list.size());
    }

    @Test
    public void getSalesReportEmptyCategoryTest() throws ApiException {
        SalesReportForm salesReportForm = TestUtils.getSalesReportForm("nike","");
        List<SalesReportData> list = reportDto.getSalesReport(salesReportForm);
        assertEquals(1,list.size());
    }

    @Test
    public void getSalesReportTest() throws ApiException {
        SalesReportForm salesReportForm = TestUtils.getSalesReportForm("nike","shoes");
        List<SalesReportData> list = reportDto.getSalesReport(salesReportForm);
        assertEquals(1,list.size());
    }

    @Test
    public void getDailyReportTest() throws ApiException {
        reportDto.generateDailySalesReport();
        List<DailySalesReportData> list = reportDto.getDailySalesReport();
        assertEquals(1,list.size());
    }
}
