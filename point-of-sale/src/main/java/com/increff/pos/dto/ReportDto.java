package com.increff.pos.dto;

import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.DailySalesReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.TimeUtil;
import com.increff.pos.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ReportDto {

    @Autowired
    private BrandService brandService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private DailySalesReportService salesReportService;

    public List<InventoryReportData> getInventoryReport(BrandForm brandForm) throws ApiException {
        List<InventoryReportData> inventoryReportDataList = new ArrayList<InventoryReportData>();
        List<BrandPojo> brandPojoList = brandService.getByBrandAndCategory(brandForm.getBrand(), brandForm.getCategory());

        for(BrandPojo brand : brandPojoList) {
            int totalQuantity = getProductQuantityForBrandCategory(brand.getId());
            InventoryReportData inventoryReportData = setInventoryReportData(brand.getBrand(), brand.getCategory(), totalQuantity);
            inventoryReportDataList.add(inventoryReportData);
        }
        return inventoryReportDataList;
    }

    private Integer getProductQuantityForBrandCategory(Integer brandId) throws ApiException {
        int totalQuantity = 0;
        List<ProductPojo> productPojoList = productService.getByBrandCategoryId(brandId);
        for(ProductPojo product : productPojoList) {
            InventoryPojo inventory = inventoryService.getByProductId(product.getId());
            totalQuantity += inventory.getQuantity();
        }
        return totalQuantity;
    }

    private InventoryReportData setInventoryReportData(String brand, String category, Integer quantity) {
        InventoryReportData inventoryReportData = new InventoryReportData();
        inventoryReportData.setBrand(brand);
        inventoryReportData.setCategory(category);
        inventoryReportData.setQuantity(quantity);
        return inventoryReportData;
    }

    public List<BrandData> getBrandCategoryReport(BrandForm brandForm) throws ApiException {
        List<BrandData> brandDataList = new ArrayList<BrandData>();
        List<BrandPojo> brandCategoryList = brandService.getByBrandAndCategory(brandForm.getBrand(), brandForm.getCategory());
        for (BrandPojo brandPojo : brandCategoryList) {
            brandDataList.add(ConversionUtil.getBrandData(brandPojo));
        }
        return brandDataList;
    }

    private List<SalesReportData> initializeSalesReportData(List<BrandPojo> brandPojoList) {
        List<SalesReportData> salesReportDataList = new ArrayList<SalesReportData>();
        for(BrandPojo brandPojo : brandPojoList) {
            SalesReportData salesReportData = new SalesReportData(brandPojo, 0, 0.0);
            salesReportDataList.add(salesReportData);
        }
        return salesReportDataList;
    }

    private List<ProductPojo> getProductListFromOrderItems(List<OrderItemPojo> orderItemPojoList) throws ApiException {
        List<ProductPojo> productPojoList = new ArrayList<ProductPojo>();
        for(OrderItemPojo orderItemPojo : orderItemPojoList) {
            ProductPojo productPojo = productService.get(orderItemPojo.getProductId());
            productPojoList.add(productPojo);
        }
        return productPojoList;
    }

    public List<SalesReportData> getSalesReport(SalesReportForm salesReportForm) throws ApiException {
        NormalizeUtil.normalizeSalesReport(salesReportForm);
        List<BrandPojo> brandPojoList = brandService.getByBrandAndCategory(salesReportForm.getBrand(), salesReportForm.getCategory());
        List<SalesReportData> salesReportDataList = initializeSalesReportData(brandPojoList);
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAllOrderItemBetween(
                salesReportForm.getStartDate(), salesReportForm.getEndDate());
        List<ProductPojo> productPojoList = getProductListFromOrderItems(orderItemPojoList);

        // Calculate quantity and revenue for salesReportData
        return calculateQuantityAndRevenue(brandPojoList, productPojoList, orderItemPojoList);
    }

    public List<SalesReportData> calculateQuantityAndRevenue(
            List<BrandPojo> brandPojoList, List<ProductPojo> productPojoList, List<OrderItemPojo> orderItemPojoList
    ) {
        List<SalesReportData> salesReportDataList = initializeSalesReportData(brandPojoList);
        for (OrderItemPojo orderItem : orderItemPojoList) {
            int productId = orderItem.getProductId();
            ProductPojo product = productPojoList.stream().filter(p -> p.getId() == productId).findFirst().get();
            int brandCategoryId = product.getBrandId();
            // Find and update salesReportData
            for (SalesReportData salesReportItemDataItem : salesReportDataList) {
                if (salesReportItemDataItem.getBrandCategoryId() == brandCategoryId) {
                    salesReportItemDataItem
                            .setQuantity(salesReportItemDataItem.getQuantity() + orderItem.getQuantity());
                    salesReportItemDataItem.setRevenue(
                            salesReportItemDataItem.getRevenue()
                                    + orderItem.getQuantity() * orderItem.getSellingPrice());
                }
            }
        }
        return salesReportDataList;
    }

    public List<DailySalesReportData> getDailySalesReport() {
        List<DailySalesReportData> dailySalesReportDataList = new ArrayList<DailySalesReportData>();
        List<DailySalesReportPojo> dailySalesReportPojoList = salesReportService.getAll();
        for(DailySalesReportPojo dailySalesReportPojo : dailySalesReportPojoList) {
            dailySalesReportDataList.add(ConversionUtil.getDailySalesReportData(dailySalesReportPojo));
        }
        return dailySalesReportDataList;
    }

    private DailySalesReportPojo setDailySalesReportPojo(Date date, Double revenue, Integer orderCount, Integer itemCount) {
        DailySalesReportPojo dailySalesReportPojo = new DailySalesReportPojo();
        dailySalesReportPojo.setDate(date);
        dailySalesReportPojo.setTotalRevenue(revenue);
        dailySalesReportPojo.setInvoicedItemsCount(itemCount);
        dailySalesReportPojo.setInvoicedOrdersCount(orderCount);
        return dailySalesReportPojo;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void generateDailySalesReport() throws ApiException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday = TimeUtil.getStartOfDay(cal.getTime());

        List<OrderPojo> orderPojoList = orderService.getAllBetween(yesterday, new Date());
        List<OrderItemPojo> orderItemPojoList = orderItemService.getAllOrderItemBetween(yesterday, new Date());

        Double totalRevenue = 0d;
        for (OrderItemPojo orderItem : orderItemPojoList) {
            totalRevenue += orderItem.getSellingPrice() * orderItem.getQuantity();
        }

        DailySalesReportPojo dailySalesReportPojo = setDailySalesReportPojo(
                new Date(), totalRevenue, orderPojoList.size(), orderItemPojoList.size());

        salesReportService.add(dailySalesReportPojo);
    }
}
