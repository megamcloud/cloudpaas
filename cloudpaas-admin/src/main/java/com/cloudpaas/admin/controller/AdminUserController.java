package com.cloudpaas.admin.controller;
import com.cloudpaas.admin.dao.AdminUserDao;
import com.cloudpaas.common.constants.CommonConstants;
import com.cloudpaas.common.controller.BaseController;
import com.cloudpaas.common.model.User;
import com.google.common.collect.Lists;

import io.swagger.annotations.Api;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author 大鱼
 *
 * @date 2019年7月26日 上午10:12:55
 */
@Api
@RestController
@RequestMapping("user")
public class AdminUserController extends BaseController<AdminUserDao, User>{
    
}