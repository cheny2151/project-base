package com.cheney.controller;

import com.cheney.service.RoleService;
import com.cheney.system.response.JsonMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 角色--controller
 *
 * @author cheney
 * @date 2019-11-10
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource(name = "roleServiceImpl")
    private RoleService roleService;

    @PostMapping("/refresh_cache")
    public JsonMessage refreshCache() {
        roleService.refreshAll();
        return JsonMessage.success();
    }

}
