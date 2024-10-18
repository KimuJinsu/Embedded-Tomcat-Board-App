package com.intheeast.service;

import com.intheeast.entity.User;

public interface UserService {
    User findById(Long id);  // 유저 ID로 유저 찾기
    User save(User user);  // 유저 저장하기
}