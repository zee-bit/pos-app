package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.form.ProductSearchForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.NormalizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo add(ProductPojo productPojo) throws ApiException {
        checkIfBarcodeExists(productPojo.getBarcode());
        dao.insert(productPojo);
        return productPojo;
    }

    public ProductPojo get(Integer id) throws ApiException {
        return getIfExists(id);
    }

    public List<ProductPojo> getAll() {
        return dao.selectAll();
    }

    public ProductPojo getByBarcode(String barcode) throws ApiException {
        ProductPojo productPojo = dao.selectByBarcode(barcode);
        if(productPojo == null) {
            throw new ApiException("Product with barcode " + barcode + " does not exists");
        }
        return productPojo;
    }

    public List<ProductPojo> getByBrandCategoryId(Integer brandId) {
        return dao.selectByBrandId(brandId);
    }

    public List<ProductPojo> searchProductData(ProductSearchForm f) {
        NormalizeUtil.normalizeProductSearch(f);
        return dao.searchProductData(f.getBarcode(), f.getName());
    }

    @Transactional(rollbackOn  = ApiException.class)
    public ProductPojo update(Integer id, ProductPojo productPojo) throws ApiException {
        ProductPojo old = getIfExists(id);
        old.setProduct(productPojo.getProduct());
        old.setPrice(productPojo.getPrice());
        old.setBrandId(productPojo.getBrandId());
        dao.update(old);
        return productPojo;
    }

    public ProductPojo getIfExists(Integer id) throws ApiException {
        ProductPojo b = dao.selectById(id);
        if (b == null) {
            throw new ApiException("Product with given ID does not exist, id: " + id);
        }
        return b;
    }

    public void checkIfBarcodeExists(String barcode) throws ApiException {
        ProductPojo productPojo = dao.selectByBarcode(barcode);
        if(productPojo != null) {
            throw new ApiException("Another Product with barcode " + productPojo.getBarcode() + " already exists");
        }
    }
}
