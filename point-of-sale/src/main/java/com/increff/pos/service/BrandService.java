package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo add(BrandPojo brandPojo) throws ApiException {
        checkIfBrandAndCategoryExists(brandPojo.getBrand(), brandPojo.getCategory());
        dao.insert(brandPojo);
        return brandPojo;
    }

    public BrandPojo get(Integer id) throws ApiException {
        return getIfExists(id);
    }

    public List<BrandPojo> getAll() {
        return dao.selectAll();
    }

    public List<BrandPojo> searchByBrandCategory(String brand, String category) {
        return dao.searchByBrandCategory(brand, category);
    }

    public List<BrandPojo> getByBrandAndCategory(String brand, String category) {
        if(brand.equals("") && category.equals("")) {
            return dao.selectAll();
        }
        if(brand.equals("")) {
            return dao.selectByCategory(category);
        }
        if(category.equals("")) {
            return dao.selectByBrand(brand);
        }
        List<BrandPojo> brandPojoList = new ArrayList<BrandPojo>();
        BrandPojo brandPojo = dao.selectByBrandCategory(brand, category);
        brandPojoList.add(brandPojo);
        return brandPojoList;
    }

    @Transactional(rollbackOn  = ApiException.class)
    public BrandPojo update(Integer id, BrandPojo brandPojo) throws ApiException {
        checkIfBrandAndCategoryExists(brandPojo.getBrand(), brandPojo.getCategory());
        BrandPojo old = getIfExists(id);
        old.setBrand(brandPojo.getBrand());
        old.setCategory(brandPojo.getCategory());
        dao.update(old);
        return brandPojo;
    }

    public BrandPojo getIfExists(Integer id) throws ApiException {
        BrandPojo b = dao.selectById(id);
        if (b == null) {
            throw new ApiException("Brand with given ID does not exist, id: " + id);
        }
        return b;
    }

    public BrandPojo getIfNameAndCategoryExists(String name, String category) throws ApiException {
        BrandPojo b = dao.selectByBrandCategory(name, category);
        if (b == null) {
            throw new ApiException("Brand " + name + " with category " + category + " does not exists");
        }
        return b;
    }

    public void checkIfBrandAndCategoryExists(String name, String category) throws ApiException {
        BrandPojo b = dao.selectByBrandCategory(name, category);
        if(b != null) {
            throw new ApiException("Brand " + name + " with category " + category + " already exists");
        }
    }
}
