package com.intheeast.service.impl;

import com.intheeast.entity.User;
import com.intheeast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    @Transactional // 이 부분 추가
    public User save(User user) {
        entityManager.persist(user);
        return user;
    }
}