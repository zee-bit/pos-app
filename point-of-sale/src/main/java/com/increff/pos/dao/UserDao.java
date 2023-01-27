package com.increff.pos.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.UserPojo;

@Repository
public class UserDao extends AbstractDao {

    private static String select_email = "select p from UserPojo p where email=:email";
    private static String select_all = "select p from UserPojo p";


    @Transactional
    public void insert(UserPojo p) {
        em.persist(p);
    }

    public UserPojo selectByEmail(String email) {
        TypedQuery<UserPojo> query = getQuery(select_email, UserPojo.class);
        query.setParameter("email", email);
        return getSingle(query);
    }

    public List<UserPojo> selectAll() {
        TypedQuery<UserPojo> query = getQuery(select_all, UserPojo.class);
        return query.getResultList();
    }
}