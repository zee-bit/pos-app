package com.increff.pos.dto;

import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.UploadProgressData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.utils.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BrandDtoTest extends AbstractUnitTest {

    @Autowired
    private BrandDto brandDto;
    @Autowired
    private BrandService brandService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void addBrandTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm(" Apple ", "  ElecTronIcs");
        BrandData brandData = brandDto.add(brandForm);
        BrandPojo brandPojo = brandService.getIfNameAndCategoryExists("apple", "electronics");
        assertEquals(brandPojo.getId(), brandData.getId());
        assertEquals(brandPojo.getBrand(), brandData.getBrand());
        assertEquals(brandPojo.getCategory(), brandData.getCategory());
    }

    @Test
    public void addDuplicateBrandTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("Apple", "ElecTronIcs");
        BrandForm newBrandForm = TestUtils.getBrandForm(" Apple", "electronics");
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand apple with category electronics already exists");
        brandDto.add(brandForm);
        brandDto.add(newBrandForm); // This should throw the exception
    }

    @Test
    public void addNullBrandTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm(null, null);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand or Category cannot be empty.");
        brandDto.add(brandForm);
    }

    @Test
    public void getAllBrandTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("apple", "electronics");
        BrandForm newBrandForm = TestUtils.getBrandForm("nike", "shoes");
        BrandData brandData = brandDto.add(brandForm);
        BrandData newBrandData = brandDto.add(newBrandForm);
        BrandPojo brandPojo = brandService.getIfNameAndCategoryExists("apple", "electronics");
        BrandPojo newBrandPojo = brandService.getIfNameAndCategoryExists("nike", "shoes");
        List<BrandData> brandDataList = brandDto.getAll();
        assertEquals(2, brandDataList.size());
    }

    @Test
    public void getFilteredBrandTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("apple", "electronics");
        BrandForm filteredBrandForm = TestUtils.getBrandForm("app", null);
        BrandData brandData = brandDto.add(brandForm);
        BrandPojo brandPojo = brandService.getIfNameAndCategoryExists("apple", "electronics");
        List<BrandData> brandDataList = brandDto.getFilteredBrandCategory(filteredBrandForm);
        assertEquals(1, brandDataList.size());
        assertEquals(brandPojo.getId(), brandDataList.get(0).getId());
        assertEquals(brandPojo.getBrand(), brandDataList.get(0).getBrand());
        assertEquals(brandPojo.getCategory(), brandDataList.get(0).getCategory());
    }

    @Test
    public void getBrandByInvalidIdTest() throws ApiException {
        BrandForm BrandForm = TestUtils.getBrandForm("nike", "shoes");
        brandDto.add(BrandForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand with given ID does not exist, id: 10");
        brandDto.get(10);
    }

    @Test
    public void updateBrandTest() throws ApiException {
        BrandForm brandForm = TestUtils.getBrandForm("ApPlE", "Electronics");
        BrandData brandData = brandDto.add(brandForm);
        BrandForm updatedBrandForm = TestUtils.getBrandForm("Dell", "electronicS ");
        brandDto.update(brandData.getId(), updatedBrandForm);
        BrandData updatedBrandData = brandDto.get(brandData.getId());
        assertEquals("dell", updatedBrandData.getBrand());
        assertEquals("electronics", updatedBrandData.getCategory());
    }

    @Test
    public void addBrandFromFile() throws IOException {
        FileReader file = new FileReader("testFiles/brand.tsv");
        UploadProgressData uploadProgressData = brandDto.addBrandCategoryFromFile(file);
        assertEquals((Integer) 3, uploadProgressData.getTotalCount());
        assertEquals((Integer) 3, uploadProgressData.getSuccessCount());
        assertEquals((Integer) 0, uploadProgressData.getErrorCount());
    }
}
