package com.increff.pos.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.UploadProgressData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.InventorySearchForm;
import com.increff.pos.model.form.ProductSearchForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.ConversionUtil;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;


    public InventoryData get(String barcode) throws ApiException {
        barcode = StringUtil.toLowerCase(barcode);
        ProductPojo productPojo = productService.getByBarcode(barcode);
        InventoryPojo inventoryPojo = inventoryService.getByProductId(productPojo.getId());
        return ConversionUtil.getInventoryData(inventoryPojo, productPojo.getProduct(), barcode);
    }

    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> list = inventoryService.getAll();
        List<InventoryData> list2 = new ArrayList<InventoryData>();
        for (InventoryPojo i : list) {
            ProductPojo p = productService.getIfExists(i.getProductId());
            list2.add(ConversionUtil.getInventoryData(i, p.getProduct(), p.getBarcode()));
        }
        return list2;
    }

    public List<InventoryData> searchInventory(InventorySearchForm form) throws ApiException {
        ProductSearchForm productSearchForm = ConversionUtil.getInventorySearchFormFromProductSearchForm(form);
        List<ProductPojo> productMasterPojoList = productService.searchProductData(productSearchForm);
        List<Integer> productIds = productMasterPojoList.stream().map(o -> o.getId()).collect(Collectors.toList());
        // filter according to product id list
        List<InventoryPojo> list = inventoryService.getAll().stream()
                .filter(o -> (productIds.contains(o.getProductId()))).collect(Collectors.toList());
        List<InventoryData> list2 = new ArrayList<InventoryData>();
        for (InventoryPojo i : list) {
            ProductPojo p = productService.getIfExists(i.getProductId());
            list2.add(ConversionUtil.getInventoryData(i, p.getProduct(), p.getBarcode()));
        }
        return list2;
    }

    public InventoryData update(InventoryForm f) throws ApiException {
        validateFields(f);
        NormalizeUtil.normalizeInventory(f);
        ProductPojo productPojo = productService.getByBarcode(f.getBarcode());
        InventoryPojo inventoryPojo = ConversionUtil.getInventoryPojo(f, productPojo.getId());
        return ConversionUtil.getInventoryData(
                inventoryService.update(productPojo.getId(), inventoryPojo),
                productPojo.getProduct(), productPojo.getBarcode()
        );
    }

    public UploadProgressData addInventoryFromFile(FileReader file) {
        UploadProgressData progress = new UploadProgressData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<InventoryForm> formList = new CsvToBeanBuilder(file)
                    .withSeparator('\t')
                    .withType(InventoryForm.class)
                    .build()
                    .parse();

            progress.setTotalCount(formList.size());
            for (InventoryForm form : formList) {
                try {
                    update(form);
                    progress.setSuccessCount(progress.getSuccessCount() + 1);
                } catch (ApiException e) {
                    progress.setErrorCount(progress.getErrorCount() + 1);
                    String errorMsg = mapper.writeValueAsString(form) + " :: " + e.getMessage();
                    progress.getErrorMessages().add(errorMsg);
                }
            }
            return progress;
        } catch (Exception e) {
            progress.setErrorCount(progress.getErrorCount() + 1);
            progress.getErrorMessages().add(e.getMessage());
        }
        return progress;
    }

    public void validateFields(InventoryForm form) throws ApiException {
        if (form.getQuantity() < 0) {
            throw new ApiException("Quantity cannot be negative");
        }
    }
}
