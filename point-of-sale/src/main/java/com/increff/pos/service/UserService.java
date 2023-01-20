package com.increff.pos.service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class UserService {

    private final UserDao dao;

    @Value("${admins}")
    String adminEmails;

    private Set<String> adminSet;

    @PostConstruct
    private void init() {
        adminSet = new HashSet<>();
        for (String email : adminEmails.split(",")) {
            adminSet.add(email.trim());
        }
    }

    @Autowired
    public UserService(UserDao dao) {
        this.dao = dao;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void add(UserPojo user) throws ApiException {
        normalizeUser(user);
        UserPojo existing = dao.selectByEmail(user.getEmail());
        if (existing != null) {
            throw new ApiException("User with given email already exists");
        }

        if (adminSet.contains(user.getEmail())) {
            user.setRole("supervisor");
        } else if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("operator");
        }

        dao.insert(user);
    }

    @Transactional(rollbackOn = ApiException.class)
    public UserPojo get(String email) throws ApiException {
        return dao.selectByEmail(email);
    }

    @Transactional
    public List<UserPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional
    public void delete(int id) throws ApiException {
        dao.delete(id);
    }

    private static void normalizeUser(UserPojo user) {
        user.setEmail(StringUtil.toLowerCase(user.getEmail()));
        user.setRole(StringUtil.toLowerCase(user.getRole()));
    }

}
