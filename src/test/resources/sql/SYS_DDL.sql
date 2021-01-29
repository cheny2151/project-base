CREATE TABLE `auth_user`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(32)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(500) NOT NULL COMMENT '密码',
    `enabled`     tinyint      NOT NULL DEFAULT 1 COMMENT '是否启用',
    `create_date` datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统用户';

CREATE TABLE `sys_role`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(32)  NOT NULL COMMENT '角色名',
    `code`        VARCHAR(500) NOT NULL COMMENT '角色编码',
    `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色';

CREATE TABLE `mid_user_role`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(32)  NOT NULL COMMENT '用户名',
    `role_code`   VARCHAR(500) NOT NULL COMMENT '角色编码',
    `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户角色中间表';

CREATE TABLE `sys_auth`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `code`        VARCHAR(32)  NOT NULL COMMENT '权限编码',
    `url_pattern` VARCHAR(500) NOT NULL COMMENT '权限url',
    `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统权限';

CREATE TABLE `mid_role_auth`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `role_code`   VARCHAR(500) NOT NULL COMMENT '角色编码',
    `auth_code`   VARCHAR(32)  NOT NULL COMMENT '权限编码',
    `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_role` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色权限中间表';

