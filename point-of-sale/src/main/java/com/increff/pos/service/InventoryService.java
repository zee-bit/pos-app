package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    InventoryDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryPojo inventoryPojo) throws ApiException {
        dao.insert(inventoryPojo);
    }

    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    public InventoryPojo getByProductId(Integer id) throws ApiException {
        InventoryPojo inventoryPojo = dao.selectByProductId(id);
        if(inventoryPojo == null) {
            throw new ApiException("Product with ID " + id + " does not exists");
        }
        return inventoryPojo;
    }

    @Transactional(rollbackOn  = ApiException.class)
    public InventoryPojo update(Integer productId, InventoryPojo inventoryPojo) throws ApiException {
        InventoryPojo old = dao.selectByProductId(productId);
        old.setQuantity(inventoryPojo.getQuantity());
        dao.update(old);
        return inventoryPojo;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void initialize(Integer productId) throws ApiException {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setQuantity(0);
        inventoryPojo.setProductId(productId);
        dao.insert(inventoryPojo);
    }

    // Reduce inventory quantity
    @Transactional(rollbackOn = ApiException.class)
    public void reduce(String barcode, Integer productId, Integer quantity) throws ApiException {
        InventoryPojo inventoryPojo = getByProductId(productId);
        if (inventoryPojo.getQuantity() < quantity) {
            throw new ApiException("Not enough quantity available for product, barcode:" + barcode);
        }
        inventoryPojo.setQuantity(inventoryPojo.getQuantity() - quantity);
        dao.update(inventoryPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void increase(Integer productId, Integer quantity) throws ApiException {
        InventoryPojo p = getByProductId(productId);
        p.setQuantity(p.getQuantity() + quantity);
        dao.update(p);
    }
}
