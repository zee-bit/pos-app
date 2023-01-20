package com.increff.pos.service;

import com.increff.pos.dao.SalesReportDao;
import com.increff.pos.pojo.DailySalesReportPojo;
import com.increff.pos.service.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class DailySalesReportService {

    @Autowired
    private SalesReportDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public DailySalesReportPojo add(DailySalesReportPojo salesReportPojo) throws ApiException {
        dao.insert(salesReportPojo);
        return salesReportPojo;
    }

    public List<DailySalesReportPojo> getAll() {
        return dao.selectAll();
    }

    public List<DailySalesReportPojo> getAllBetween(Date startingDate, Date endingDate) {
        return dao.selectAllBetween(startingDate, endingDate);
    }
}
