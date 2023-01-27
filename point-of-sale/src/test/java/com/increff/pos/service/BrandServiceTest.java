package com.increff.pos.service;

import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.utils.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BrandServiceTest extends AbstractUnitTest {

    @Autowired
    private BrandService brandService;
    @Autowired
    private BrandDao brandDao;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void addBrandCategoryTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        BrandPojo actualPojo = brandService.add(brandPojo);
        BrandPojo expectedPojo = brandDao.selectByBrandCategory("apple", "electronics");
        assertEquals(expectedPojo.getId(), actualPojo.getId());
        assertEquals(expectedPojo.getBrand(), actualPojo.getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojo.getCategory());
    }

    @Test
    public void addDuplicateBrandTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        brandDao.insert(brandPojo);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand apple with category electronics already exists");
        brandService.add(brandPojo); // This should throw the exception
    }

    @Test
    public void getAllBrandTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        BrandPojo newBrandPojo = TestUtils.getBrandPojo("nike", "shoes");
        brandDao.insert(brandPojo);
        brandDao.insert(newBrandPojo);
        List<BrandPojo> expectedBrandPojoList = brandDao.selectAll();
        List<BrandPojo> actualBrandPojoList = brandService.getAll();
        assertEquals(expectedBrandPojoList.size(), actualBrandPojoList.size());
    }

    @Test
    public void getBrandByIdTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        brandDao.insert(brandPojo);
        BrandPojo expectedPojo = brandDao.selectByBrandCategory("apple", "electronics");
        BrandPojo actualPojo = brandService.get(expectedPojo.getId());
        assertEquals(expectedPojo.getBrand(), actualPojo.getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojo.getCategory());

        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand with given ID does not exist, id: 5");
        brandService.get(5);
    }

    @Test
    public void getIfNameAndCategoryExistTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        brandDao.insert(brandPojo);
        BrandPojo expectedPojo = brandDao.selectByBrandCategory("apple", "electronics");
        BrandPojo actualPojo = brandService.getIfNameAndCategoryExists("apple", "electronics");
        assertEquals(expectedPojo.getId(), actualPojo.getId());
        assertEquals(expectedPojo.getBrand(), actualPojo.getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojo.getCategory());

        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand levis with category shirt does not exists");
        brandService.getIfNameAndCategoryExists("levis", "shirt");
    }

    @Test
    public void getByBrandAndCategoryTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        brandDao.insert(brandPojo);
        BrandPojo expectedPojo = brandDao.selectByBrandCategory("apple", "electronics");
        List<BrandPojo> actualPojoByBrand = brandService.getByBrandAndCategory("apple", "");
        assertEquals(expectedPojo.getId(), actualPojoByBrand.get(0).getId());
        assertEquals(expectedPojo.getBrand(), actualPojoByBrand.get(0).getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojoByBrand.get(0).getCategory());

        List<BrandPojo> actualPojoByCategory = brandService.getByBrandAndCategory("", "electronics");
        assertEquals(expectedPojo.getId(), actualPojoByCategory.get(0).getId());
        assertEquals(expectedPojo.getBrand(), actualPojoByCategory.get(0).getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojoByCategory.get(0).getCategory());

        List<BrandPojo> actualPojoBySearch = brandService.searchByBrandCategory("app", "");
        assertEquals(expectedPojo.getId(), actualPojoByCategory.get(0).getId());
        assertEquals(expectedPojo.getBrand(), actualPojoByCategory.get(0).getBrand());
        assertEquals(expectedPojo.getCategory(), actualPojoByCategory.get(0).getCategory());
    }

    @Test
    public void updateBrandTest() throws ApiException {
        BrandPojo brandPojo = TestUtils.getBrandPojo("apple", "electronics");
        brandDao.insert(brandPojo);
        BrandPojo updatedBrandPojo = TestUtils.getBrandPojo("dell", "electronics");
        brandService.update(brandPojo.getId(), updatedBrandPojo);
        BrandPojo expectedBrandPojo = brandDao.selectByBrandCategory("dell", "electronics");
        BrandPojo actualBrandPojo = brandService.get(expectedBrandPojo.getId());
        assertEquals(expectedBrandPojo.getId(), actualBrandPojo.getId());
        assertEquals(expectedBrandPojo.getBrand(), actualBrandPojo.getBrand());
        assertEquals(expectedBrandPojo.getCategory(), actualBrandPojo.getCategory());
    }
}
