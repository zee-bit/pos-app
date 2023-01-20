package com.increff.invoice.util;

import com.increff.invoice.model.OrderDetailData;
import com.increff.invoice.model.OrderItemData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class XMLUtil {
    public static void createXml(OrderDetailData orderDetailsData)
            throws ParserConfigurationException, TransformerException {
        String xmlFilePath = "billDataXML.xml";

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        Document document = documentBuilder.newDocument();

        int i = 0;
        // root element
        Element root = document.createElement("bill");
        document.appendChild(root);
        double finalBill = 0;

        Element date = document.createElement("date");
        date.appendChild(document.createTextNode(getDate()));
        root.appendChild(date);

        Element time = document.createElement("time");
        time.appendChild(document.createTextNode(getTime()));
        root.appendChild(time);

        Element orderId = document.createElement("orderId");
        orderId.appendChild(document.createTextNode(String.valueOf(orderDetailsData.getId())));
        root.appendChild(orderId);
        // Create elements from BillData list
        List<OrderItemData> orderItemsData = orderDetailsData.getOrderItems();
        for (i = 0; i < orderItemsData.size(); i++) {
            Element item = document.createElement("item");
            root.appendChild(item);
            Element id = document.createElement("id");
            id.appendChild(document.createTextNode(String.valueOf(orderItemsData.get(i).getBarcode())));
            item.appendChild(id);

            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(orderItemsData.get(i).getProductName()));
            item.appendChild(name);
            // Calculate total bill amount
            finalBill = finalBill + orderItemsData.get(i).getQuantity() * orderItemsData.get(i).getSellingPrice();
            Element quantity = document.createElement("quantity");
            quantity.appendChild(document.createTextNode(String.valueOf(orderItemsData.get(i).getQuantity())));
            item.appendChild(quantity);

            Element mrp = document.createElement("mrp");
            mrp.appendChild(document.createTextNode(String.valueOf(orderItemsData.get(i).getSellingPrice())));
            item.appendChild(mrp);

        }

        Element total = document.createElement("total");
        total.appendChild(document.createTextNode(String.valueOf(finalBill) + " Rs."));
        root.appendChild(total);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);

        StreamResult streamResult = new StreamResult(new File(xmlFilePath));

        transformer.transform(domSource, streamResult);
    }

    // Get date in required format
    private static String getDate() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date dateobj = new Date();
        String date = df.format(dateobj);
        return date;
    }

    // Get time in required format
    private static String getTime() {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date dateobj = new Date();
        String time = df.format(dateobj);
        return time;
    }

}
