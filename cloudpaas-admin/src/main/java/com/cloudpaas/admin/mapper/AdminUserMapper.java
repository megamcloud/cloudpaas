package com.cloudpaas.admin.mapper;

import com.cloudpaas.common.model.User;

import tk.mybatis.mapper.common.Mapper;

public interface AdminUserMapper extends Mapper<User> {
	
	public User login(User user);
}