package com.increff.pos.dto;

import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.UploadProgressData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InventoryDtoTest extends AbstractUnitTest {

    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

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
    }

    @Test
    public void getInventoryQuantityTest() throws ApiException {
        InventoryData inventoryData = inventoryDto.get("NK123");
        assertEquals((Integer)0, inventoryData.getQuantity());
    }

    @Test
    public void getAllInventoryTest() throws ApiException {
        List<InventoryData> inventoryDataList = inventoryDto.getAll();
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        assertEquals(inventoryPojoList.size(), inventoryDataList.size());
    }

    @Test
    public void getInventoryByInvalidBarcodeTest() throws ApiException {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with barcode nk012 does not exists");
        InventoryData inventoryData = inventoryDto.get("NK012");
    }

    @Test
    public void updateInventoryTest() throws ApiException {
        InventoryForm inventoryForm = TestUtils.getInventoryForm("NK123",50);
        InventoryData inventoryData = inventoryDto.update(inventoryForm);
        ProductPojo productPojo = productService.getByBarcode("nk123");
        InventoryPojo pojo = inventoryService.getByProductId(productPojo.getId());
        assertEquals(productPojo.getBarcode(), inventoryData.getBarcode());
        assertEquals(pojo.getQuantity(), inventoryData.getQuantity());
        assertEquals(productPojo.getProduct(),inventoryData.getProduct());
    }

    @Test
    public void updateInventoryNegativeQuantityTest() throws ApiException {
        InventoryForm inventoryForm = TestUtils.getInventoryForm("NK123",-20);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Quantity cannot be negative");
        InventoryData inventoryData = inventoryDto.update(inventoryForm);
    }

    @Test
    public void addBrandFromFile() throws IOException, ApiException {
        FileReader file = new FileReader("testFiles/inventory.tsv");
        UploadProgressData uploadProgressData = inventoryDto.addInventoryFromFile(file);
        assertEquals((Integer) 2, uploadProgressData.getTotalCount());
        assertEquals((Integer) 2, uploadProgressData.getSuccessCount());
        assertEquals((Integer) 0, uploadProgressData.getErrorCount());
    }
}
