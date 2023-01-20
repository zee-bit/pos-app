package com.increff.invoice.controller;

import com.increff.invoice.model.OrderDetailData;
import com.increff.invoice.util.PDFUtil;
import com.increff.invoice.util.XMLUtil;
import io.swagger.annotations.Api;
import org.apache.commons.io.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Base64;

@Api
@RestController
public class InvoiceApiController {

    @RequestMapping(value = "", consumes = "application/json")
    public String encodeOrderDetails(@RequestBody OrderDetailData orderDetails) throws Exception {

        System.out.println(orderDetails);
        XMLUtil.createXml(orderDetails);
        PDFUtil.createPDF();

        byte[] bytes = FileUtils.readFileToByteArray(new File("bill.pdf"));
        String base64PdfStr = Base64.getEncoder().encodeToString(bytes);

        System.out.println("Sending: " + base64PdfStr);
        return base64PdfStr;
    }

}
