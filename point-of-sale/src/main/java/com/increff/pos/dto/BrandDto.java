package com.increff.pos.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.UploadProgressData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.ConversionUtil;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.CsvToBeanFilter;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BrandDto {

    @Autowired
    private BrandService service;

    public BrandData add(BrandForm form) throws ApiException {
        validateFields(form);
        NormalizeUtil.normalizeBrandCategory(form);
        BrandPojo brandPojo = ConversionUtil.getBrandPojo(form);
        return ConversionUtil.getBrandData(service.add(brandPojo));
    }

    public BrandData get(Integer id) throws ApiException {
        BrandPojo brandPojo = service.get(id);
        return ConversionUtil.getBrandData(brandPojo);
    }

    public List<BrandData> getAll() {
        return service.getAll()
                .stream()
                .map(obj -> ConversionUtil.getBrandData(obj))
                .collect(Collectors.toList());
    }

    public List<BrandData> getFilteredBrandCategory(BrandForm form) {
        setBrandForm(form);
        NormalizeUtil.normalizeBrandCategory(form);
        List<BrandPojo> list = service.searchByBrandCategory(form.getBrand(), form.getCategory());
        return list.stream().map(obj -> ConversionUtil.getBrandData(obj)).collect(Collectors.toList());
    }

    public BrandData update(Integer id, BrandForm f) throws ApiException {
        validateFields(f);
        NormalizeUtil.normalizeBrandCategory(f);
        BrandPojo b = ConversionUtil.getBrandPojo(f);
        return ConversionUtil.getBrandData(service.update(id, b));
    }

    public UploadProgressData addBrandCategoryFromFile(FileReader file) throws ApiException {
        UploadProgressData progress = new UploadProgressData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<BrandForm> formList = new CsvToBeanBuilder(file).withSeparator('\t').withType(BrandForm.class).build().parse();
            progress.setTotalCount(formList.size());
            for (BrandForm form : formList) {
                try {
                    add(form);
                    progress.setSuccessCount(progress.getSuccessCount() + 1);
                } catch (ApiException e) {
                    progress.setErrorCount(progress.getErrorCount() + 1);
                    progress.getErrorMessages().add(mapper.writeValueAsString(form) + " :: " + e.getMessage());
                }
            }
            return progress;
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    public void validateFields(BrandForm form) throws ApiException {
        if (StringUtil.isEmpty(form.getBrand()) || StringUtil.isEmpty(form.getCategory())) {
            throw new ApiException("Brand or Category cannot be empty.");
        }
    }

    public void setBrandForm(BrandForm form) {
        if (form.getBrand() == null) {
            form.setBrand("");
        }
        if (form.getCategory() == null) {
            form.setCategory("");
        }
    }
}
