package com.increff.pos.controller.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.UploadProgressData;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class FileController {

    @Autowired
    private BrandDto brandDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private InventoryDto inventoryDto;

    /**
     * Upload single file using Spring Controller
     */
    @ResponseBody
    @RequestMapping(value = "/upload/file", method = RequestMethod.POST)
    public UploadProgressData uploadFileHandler(HttpServletRequest req, HttpServletResponse resp) throws ApiException {

        MultipartHttpServletRequest multipartReq = (MultipartHttpServletRequest) req;
        MultipartFile file = multipartReq.getFile("temp");
        String tsvType = req.getParameter("type");

        FileReader serverFileReader = FileUtil.processUploadedFiles(file);

        if(Objects.equals(tsvType, "brand")) {
            UploadProgressData response = brandDto.addBrandCategoryFromFile(serverFileReader);
            FileUtil.createErrorFile(response.getErrorMessages());
            return response;
        }
        else if(Objects.equals(tsvType, "product")) {
            UploadProgressData response = productDto.addProductFromFile(serverFileReader);
            FileUtil.createErrorFile(response.getErrorMessages());
            return response;
        }
        else if(Objects.equals(tsvType, "inventory")) {
            UploadProgressData response = inventoryDto.addInventoryFromFile(serverFileReader);
            FileUtil.createErrorFile(response.getErrorMessages());
            return response;
        }
        return new UploadProgressData();
    }

    /**
     * Download error file using Spring Controller
     */
    @RequestMapping(value = "/download/error", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadErrorFile() throws IOException {
        String errorDirPath = "/home/zean/pos/errorFiles";
        File file = new File(errorDirPath + File.separator + "error.txt");
        HttpHeaders header = getHttpHeaders("error.txt");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("text/plain"))
                .body(resource);
    }

    /**
     * Download invoice file using Spring Controller
     */
    @RequestMapping(value = "/download/invoice/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadInvoicePdf(@PathVariable Integer id) throws IOException {
        String invoiceDirPath = "/home/zean/pos/invoiceFiles";
        File file = new File(invoiceDirPath + File.separator + "bill" + id.toString() + ".pdf");
        HttpHeaders header = getHttpHeaders("invoice.pdf");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);
    }

    private HttpHeaders getHttpHeaders(String filename) {
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return header;
    }
}