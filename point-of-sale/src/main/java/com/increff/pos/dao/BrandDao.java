package com.increff.pos.dao;

import com.increff.pos.pojo.BrandPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class BrandDao extends AbstractDao {
    private String select_all = "select b from BrandPojo b";
    private String select_id = "select b from BrandPojo b where id=:id";
    private String update_id = "update BrandPojo b set b.brand=:brand, b.category=:category where id=:id";
    private String select_br_cat = "select b from BrandPojo b where brand=:brand and category=:category";
    private String select_brand = "select b from BrandPojo b where brand=:brand";
    private String select_category = "select b from BrandPojo b where category=:category";
    private String search = "select b from BrandPojo b where brand like :brand and category like :category";

    @Transactional
    public void insert(BrandPojo brandPojo) {
        em.persist(brandPojo);
    }

    public BrandPojo selectById(Integer id) {
        TypedQuery<BrandPojo> query = getQuery(select_id, BrandPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<BrandPojo> selectAll() {
        TypedQuery<BrandPojo> query = getQuery(select_all, BrandPojo.class);
        return query.getResultList();
    }

    public BrandPojo selectByBrandCategory(String brand, String category) {
        TypedQuery<BrandPojo> query = getQuery(select_br_cat, BrandPojo.class);
        query.setParameter("brand", brand);
        query.setParameter("category", category);
        return getSingle(query);
    }

    public List<BrandPojo> searchByBrandCategory(String brand, String category) {
        TypedQuery<BrandPojo> query = getQuery(search, BrandPojo.class);
        query.setParameter("brand", brand+"%");
        query.setParameter("category", category+"%");
        return query.getResultList();
    }

    public List<BrandPojo> selectByBrand(String brand) {
        TypedQuery<BrandPojo> query = getQuery(select_brand, BrandPojo.class);
        query.setParameter("brand", brand);
        return query.getResultList();
    }

    public List<BrandPojo> selectByCategory(String category) {
        TypedQuery<BrandPojo> query = getQuery(select_category, BrandPojo.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

    public void update(BrandPojo brandPojo) {
        // Update using EM method
        em.merge(brandPojo);
    }
}
