package com.increff.pos.util;

import com.increff.pos.model.form.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class NormalizeUtil {

    public static Double normalizeDouble(Double input) {
        String[] parts = input.toString().split("\\.");
        if (parts.length == 1) return input;

        String integerPart = parts[0];
        String decimalPart = parts[1];

        if (decimalPart.length() <= 2) return input;
        decimalPart = decimalPart.substring(0, 2);

        String doubleStr = integerPart + "." + decimalPart;
        return Double.parseDouble(doubleStr);
    }

    public static void normalizeBrandCategory(BrandForm brand) {
        brand.setBrand(StringUtil.toLowerCase(brand.getBrand()));
        brand.setCategory(StringUtil.toLowerCase(brand.getCategory()));
    }

    public static void normalizeProduct(ProductForm form) {
        form.setPrice(normalizeDouble(form.getPrice()));
        form.setProduct(StringUtil.toLowerCase(form.getProduct()));
        form.setBarcode(StringUtil.toLowerCase(form.getBarcode()));
        form.setBrandName(StringUtil.toLowerCase(form.getBrandName()));
        form.setBrandCategory(StringUtil.toLowerCase(form.getBrandCategory()));
    }

    public static void normalizeInventory(InventoryForm form) {
        form.setBarcode(StringUtil.toLowerCase(form.getBarcode()));
    }

    public static void normalizeOrderItem(List<OrderItemForm> orderItemFormList) {
        for (OrderItemForm orderItemForm : orderItemFormList) {
            orderItemForm.setBarcode(StringUtil.toLowerCase(orderItemForm.getBarcode()));
        }
    }

    public static void normalizeSalesReport(SalesReportForm salesReportForm) {
        salesReportForm.setBrand(StringUtil.toLowerCase(salesReportForm.getBrand()));
        salesReportForm.setCategory(StringUtil.toLowerCase(salesReportForm.getCategory()));
        if(salesReportForm.getStartDate() == null) {
            salesReportForm.setStartDate(new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime());
        }
        if(salesReportForm.getEndDate() == null) {
            salesReportForm.setEndDate(new Date());
        }
        salesReportForm.setStartDate(TimeUtil.getStartOfDay(salesReportForm.getStartDate()));
        salesReportForm.setEndDate(TimeUtil.getEndOfDay(salesReportForm.getEndDate()));
    }
}
