package com.increff.pos.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.UploadProgressData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.ConversionUtil;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDto {

    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private InventoryService inventoryService;


    @Transactional(rollbackFor = ApiException.class)
    public ProductData add(ProductForm form) throws ApiException {
        validateFields(form);
        normalize(form);
        BrandPojo brandPojo = brandService.getIfNameAndCategoryExists(form.getBrandName(), form.getBrandCategory());
        ProductPojo productPojo = ConversionUtil.getProductPojo(form, brandPojo.getId());
        productService.add(productPojo);
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setQuantity(0);
        inventoryPojo.setProductId(productPojo.getId());
        inventoryService.add(inventoryPojo);
        return ConversionUtil.getProductData(productPojo, brandPojo.getBrand(), brandPojo.getCategory());
    }

    public ProductData get(Integer id) throws ApiException {
        ProductPojo productPojo = productService.get(id);
        BrandPojo brandPojo = brandService.get(productPojo.getBrandId());
        return ConversionUtil.getProductData(productPojo, brandPojo.getBrand(), brandPojo.getCategory());
    }

    public ProductData getByBarcode(String barcode) throws ApiException {
        ProductPojo productPojo = productService.getByBarcode(barcode);
        BrandPojo brandPojo = brandService.get(productPojo.getBrandId());
        return ConversionUtil.getProductData(productPojo, brandPojo.getBrand(), brandPojo.getCategory());
    }

    public List<ProductData> getAll() throws ApiException {
        List<ProductPojo> list = productService.getAll();
        List<ProductData> list2 = new ArrayList<ProductData>();
        for (ProductPojo p : list) {
            BrandPojo b = brandService.get(p.getBrandId());
            list2.add(ConversionUtil.getProductData(p, b.getBrand(), b.getCategory()));
        }
        return list2;
    }

    public ProductData update(Integer id, ProductForm form) throws ApiException {
        validateFields(form);
        normalize(form);
        BrandPojo brandPojo = brandService.getIfNameAndCategoryExists(form.getBrandName(), form.getBrandCategory());
        ProductPojo productPojo = ConversionUtil.getProductPojo(form, brandPojo.getId());
        productPojo.setId(id);
        return ConversionUtil.getProductData(
                productService.update(id, productPojo), brandPojo.getBrand(), brandPojo.getCategory()
        );
    }

    public UploadProgressData addProductFromFile(FileReader file) {
        UploadProgressData progress = new UploadProgressData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ProductForm> formList = new CsvToBeanBuilder(file)
                    .withSeparator('\t')
                    .withType(ProductForm.class)
                    .build()
                    .parse();

            for (ProductForm form : formList) {
                try {
                    add(form);
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

    public void validateFields(ProductForm form) throws ApiException {
        if(StringUtil.isEmpty(form.getProduct()) || StringUtil.isEmpty(form.getBarcode())
                || StringUtil.isEmpty(form.getBrandName()) || StringUtil.isEmpty(form.getBrandCategory())) {
            throw new ApiException("None of the fields can be empty");
        }
        if (form.getPrice() < 0) {
            throw new ApiException("Price cannot be negative");
        }
    }

    public void normalize(ProductForm form) {
        form.setProduct(StringUtil.toLowerCase(form.getProduct()));
        form.setBarcode(StringUtil.toLowerCase(form.getBarcode()));
        form.setBrandName(StringUtil.toLowerCase(form.getBrandName()));
        form.setBrandCategory(StringUtil.toLowerCase(form.getBrandCategory()));
    }
}
