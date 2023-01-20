package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class InventoryDao extends AbstractDao {
    private String select_all = "select i from InventoryPojo i";
    private String select_productId = "select i from InventoryPojo i where productId=:productId";

    @Transactional
    public void insert(InventoryPojo inventoryPojo) {
        em.persist(inventoryPojo);
    }

    public List<InventoryPojo> selectAll() {
        TypedQuery<InventoryPojo> query = getQuery(select_all, InventoryPojo.class);
        return query.getResultList();
    }

    public InventoryPojo selectByProductId(Integer productId) {
        TypedQuery<InventoryPojo> query = getQuery(select_productId, InventoryPojo.class);
        query.setParameter("productId", productId);
        return getSingle(query);
    }

    public void update(InventoryPojo inventoryPojo) {
        // Update using EM method
        em.merge(inventoryPojo);
    }
}
