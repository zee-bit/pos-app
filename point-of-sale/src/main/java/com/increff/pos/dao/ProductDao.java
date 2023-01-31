package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao {
    private String select_all = "select p from ProductPojo p";
    private String select_id = "select p from ProductPojo p where id=:id";
    private String select_barcode = "select p from ProductPojo p where barcode=:barcode";
    private String select_brand_id = "select p from ProductPojo p where brandId=:brandId";
    private String search_product = "select p from ProductPojo p where product like :product and barcode like :barcode";

    @Transactional
    public void insert(ProductPojo productPojo) {
        em.persist(productPojo);
    }

    public ProductPojo selectById(Integer id) {
        TypedQuery<ProductPojo> query = getQuery(select_id, ProductPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<ProductPojo> selectAll() {
        TypedQuery<ProductPojo> query = getQuery(select_all, ProductPojo.class);
        return query.getResultList();
    }

    public ProductPojo selectByBarcode(String barcode) {
        TypedQuery<ProductPojo> query = getQuery(select_barcode, ProductPojo.class);
        query.setParameter("barcode", barcode);
        return getSingle(query);
    }

    public List<ProductPojo> selectByBrandId(Integer brandId) {
        TypedQuery<ProductPojo> query = getQuery(select_brand_id, ProductPojo.class);
        query.setParameter("brandId", brandId);
        return query.getResultList();
    }

    public List<ProductPojo> searchProductData(String barcode, String name) {
        TypedQuery<ProductPojo> query = getQuery(search_product, ProductPojo.class);
        query.setParameter("barcode", barcode + "%");
        query.setParameter("product", "%" + name + "%");
        return query.getResultList();
    }

    public void update(ProductPojo productPojo) {
        // Update using EM method
        em.merge(productPojo);
    }

}
