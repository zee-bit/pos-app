package com.increff.pos.dto;

import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProductDtoTest extends AbstractUnitTest {

    @Autowired
    private ProductDto productDto;
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUpBrands() throws ApiException {
        BrandPojo brandPojo1 = TestUtils.getBrandPojo("haldirams", "food");
        brandService.add(brandPojo1);

        BrandPojo brandPojo2 = TestUtils.getBrandPojo("apple", "electronics");
        brandService.add(brandPojo2);
    }

    @Test
    public void addProductTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123", " Haldirams ", "  Food", 9.99);
        ProductData productData = productDto.add(productForm);
        ProductPojo productPojo = productService.getByBarcode("ba123");
        BrandPojo brandPojo = brandService.get(productPojo.getBrandId());
        assertEquals(productPojo.getProduct(), productData.getProduct());
        assertEquals(productPojo.getBarcode(), productData.getBarcode());
        assertEquals(productPojo.getPrice(), productData.getPrice());
        assertEquals(brandPojo.getBrand(), productData.getBrandName());
        assertEquals(brandPojo.getCategory(), productData.getBrandCategory());
    }

    @Test
    public void addProductWithoutBrandTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123", " Apple ", "  Food", 9.99);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand apple with category food does not exists");
        productDto.add(productForm);
    }

    @Test
    public void addProductWithDuplicateBarcodeTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123", " Haldirams ", "  Food", 9.99);
        productDto.add(productForm);
        ProductForm newProductForm = TestUtils.getProductForm(
                "Iphone 7", "ba123", " Apple ", "  Electronics", 79999.00);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Another Product with barcode ba123 already exists");
        productDto.add(newProductForm);
    }

    @Test
    public void addProductNullBarcodeTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", null, " Haldirams ", "  Electronics", 9.99);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("None of the fields can be empty");
        productDto.add(productForm);
    }

    @Test
    public void addProductNullFieldsTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                null, "ba123", " Haldirams ", "  Food", null);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("None of the fields can be empty");
        productDto.add(productForm);
    }

    @Test
    public void getAllProductTest() throws ApiException {
        BrandPojo brandPojo1 = brandService.getIfNameAndCategoryExists("haldirams", "food");
        ProductPojo productPojo1 = TestUtils.getProductPojo("bhujiya", "ba123", brandPojo1.getId(), 9.99);
        productService.add(productPojo1);
        BrandPojo brandPojo2 = brandService.getIfNameAndCategoryExists("apple", "electronics");
        ProductPojo productPojo2 = TestUtils.getProductPojo("iphone 5", "ip123", brandPojo2.getId(), 34999d);
        productService.add(productPojo2);
        List<ProductPojo> productPojoList = productService.getAll();
        List<ProductData> productDataList = productDto.getAll();
        assertEquals(productPojoList.size(), productDataList.size());
    }

    @Test
    public void getProductByIdTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123", " Haldirams ", "  Food", 9.99);
        ProductData productData = productDto.add(productForm);
        ProductPojo productPojo = productService.getByBarcode("ba123");
        BrandPojo brandPojo = brandService.get(productPojo.getBrandId());
        assertEquals(productPojo.getProduct(), productData.getProduct());
        assertEquals(productPojo.getBarcode(), productData.getBarcode());
        assertEquals(brandPojo.getBrand(), productData.getBrandName());
        assertEquals(brandPojo.getCategory(), productData.getBrandCategory());
        assertEquals(productPojo.getPrice(), productData.getPrice());
    }

    @Test
    public void getProductByBarcodeTest() throws ApiException {
        BrandPojo brandPojo = brandService.getIfNameAndCategoryExists("haldirams", "food");
        ProductPojo pojo = TestUtils.getProductPojo("bhujiya", "ba123", brandPojo.getId(), 9.99);
        productService.add(pojo);
        ProductData productData = productDto.getByBarcode("ba123");
        ProductPojo productPojo = productService.getByBarcode("ba123");
        assertEquals(productPojo.getId(), productData.getId());
        assertEquals(productPojo.getProduct(), productData.getProduct());
        assertEquals(productPojo.getBarcode(), productData.getBarcode());
        assertEquals(brandPojo.getBrand(), productData.getBrandName());
        assertEquals(brandPojo.getCategory(), productData.getBrandCategory());
        assertEquals(productPojo.getPrice(), productData.getPrice());
    }

    @Test
    public void getProductByInvalidIdTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123", " Haldirams ", "  Food", 9.99);
        productDto.add(productForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with given ID does not exist, id: 5");
        ProductData productData = productDto.get(5);
    }

    @Test
    public void getProductByInvalidBarcodeTest() throws ApiException {
        ProductForm productForm = TestUtils.getProductForm(
                "Bhujiya", "ba123", " Haldirams ", "  Food", 9.99);
        productDto.add(productForm);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with barcode ab321 does not exists");
        ProductData productData = productDto.getByBarcode("ab321");
    }

    @Test
    public void updateProductTest() throws ApiException {
        BrandPojo brandPojo = brandService.getIfNameAndCategoryExists("apple","electronics");
        ProductPojo productPojo = TestUtils.getProductPojo("iphone", "ip123", brandPojo.getId(), 34999d);
        productService.add(productPojo);
        ProductForm productForm = TestUtils.getProductForm(" IPhone 5 ","IP123", "Apple", "Electronics", 39999d);
        ProductData updatedData = productDto.update(productPojo.getId(), productForm);
        ProductPojo pojo = productService.get(productPojo.getId());
        assertEquals(pojo.getId(), updatedData.getId());
        assertEquals(pojo.getProduct(), updatedData.getProduct());
        assertEquals(pojo.getBarcode(), updatedData.getBarcode());
        assertEquals(pojo.getPrice(), updatedData.getPrice());
        assertEquals(brandPojo.getBrand(), updatedData.getBrandName());
        assertEquals(brandPojo.getCategory(), updatedData.getBrandCategory());
    }
}
