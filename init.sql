/*
Navicat MySQL Data Transfer

Source Server         : V192.168.9.132
Source Server Version : 50735
Source Host           : 192.168.9.132:3306
Source Database       : tyfast

Target Server Type    : MYSQL
Target Server Version : 50735
File Encoding         : 65001

Date: 2022-02-12 23:25:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_sys_menu`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_menu`;
CREATE TABLE `t_sys_menu` (
`menu_id`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单ID' ,
`menu_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称' ,
`menu_alias`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单别名' ,
`parent_id`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父菜单ID' ,
`menu_type`  char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单类型(M=菜单；F=功能按钮)' ,
`icon`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标' ,
`url`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求URL' ,
`visible`  int(1) NULL DEFAULT 1 COMMENT '菜单显隐状态(1=显示；0=隐藏)' ,
`order_num`  int(4) NULL DEFAULT 1 COMMENT '显示顺序' ,
`remark`  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' ,
`create_user`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`create_time`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`update_user`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`update_time`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
PRIMARY KEY (`menu_id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='菜单权限'

;

-- ----------------------------
-- Records of t_sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `t_sys_menu` VALUES ('22021220851349000138', '系统管理', '系统管理', '0', 'M', 'mdi-view-dashboard', null, '1', '1', null, '22013040950847000084', '2022-02-12 17:47:29', '22013040950847000084', '2022-02-12 17:47:29'), ('22021220868051000138', '用户管理', '用户管理', '22021220851349000138', 'M', null, '/system/user/view', '1', '1', null, '22013040950847000084', '2022-02-12 17:47:45', '22013040950847000084', '2022-02-12 17:47:45'), ('22021220879115000138', '角色管理', '角色管理', '22021220851349000138', 'M', null, '/system/role/view', '1', '1', null, '22013040950847000084', '2022-02-12 17:47:57', '22013040950847000084', '2022-02-12 17:47:57'), ('22021220896714000138', '菜单管理', '菜单管理', '22021220851349000138', 'M', null, '/system/menu/view', '1', '1', null, '22013040950847000084', '2022-02-12 17:48:14', '22013040950847000084', '2022-02-12 17:48:14'), ('22021220927630000138', '列表', '列表', '22021220868051000138', 'F', null, '/system/user/list', '1', '1', null, '22013040950847000084', '2022-02-12 17:48:45', '22013040950847000084', '2022-02-12 17:48:45'), ('22021220938979000138', '新增', '新增', '22021220868051000138', 'F', null, '/system/user/save', '1', '1', null, '22013040950847000084', '2022-02-12 17:48:56', '22013040950847000084', '2022-02-12 17:48:56'), ('22021220948854000138', '修改', '修改', '22021220868051000138', 'F', null, '/system/user/update', '1', '1', null, '22013040950847000084', '2022-02-12 17:49:06', '22013040950847000084', '2022-02-12 17:49:06'), ('22021220958161000138', '删除', '删除', '22021220868051000138', 'F', null, '/system/user/del', '1', '1', null, '22013040950847000084', '2022-02-12 17:49:16', '22013040950847000084', '2022-02-12 17:49:16'), ('22021220969051000138', '查询明细', '查询明细', '22021220868051000138', 'F', null, '/system/user/single', '1', '1', null, '22013040950847000084', '2022-02-12 17:49:26', '22013040950847000084', '2022-02-12 17:49:26'), ('22021220981781000138', '赋权列表', '赋权列表', '22021220868051000138', 'F', null, '/system/user/grant/list', '1', '1', null, '22013040950847000084', '2022-02-12 17:49:39', '22013040950847000084', '2022-02-12 17:49:39'), ('22021220994555000138', '新增赋权', '新增赋权', '22021220868051000138', 'F', null, '/system/user/grant/save', '1', '1', null, '22013040950847000084', '2022-02-12 17:49:52', '22013040950847000084', '2022-02-12 17:49:52'), ('22021221006012000138', '删除赋权', '删除赋权', '22021220868051000138', 'F', null, '/system/user/grant/del', '1', '1', null, '22013040950847000084', '2022-02-12 17:50:03', '22013040950847000084', '2022-02-12 17:50:03'), ('22021221023125000138', '列表', '列表', '22021220879115000138', 'F', null, '/system/role/list', '1', '1', null, '22013040950847000084', '2022-02-12 17:50:21', '22013040950847000084', '2022-02-12 17:50:21'), ('22021221030073000138', '新增', '新增', '22021220879115000138', 'F', null, '/system/role/save', '1', '1', null, '22013040950847000084', '2022-02-12 17:50:27', '22013040950847000084', '2022-02-12 17:50:27'), ('22021221038308000138', '修改', '修改', '22021220879115000138', 'F', null, '/system/role/update', '1', '1', null, '22013040950847000084', '2022-02-12 17:50:36', '22013040950847000084', '2022-02-12 17:50:36'), ('22021221046144000138', '删除', '删除', '22021220879115000138', 'F', null, '/system/role/del', '1', '1', null, '22013040950847000084', '2022-02-12 17:50:44', '22013040950847000084', '2022-02-12 17:50:44'), ('22021221060910000138', '查询明细', '查询明细', '22021220879115000138', 'F', null, '/system/role/single', '1', '1', null, '22013040950847000084', '2022-02-12 17:50:58', '22013040950847000084', '2022-02-12 17:50:58'), ('22021221071941000138', '权限分配', '权限分配', '22021220879115000138', 'F', null, '/system/role/grant/save', '1', '1', null, '22013040950847000084', '2022-02-12 17:51:09', '22013040950847000084', '2022-02-12 17:51:09'), ('22021221088773000138', '列表', '列表', '22021220896714000138', 'F', null, '/system/menu/list', '1', '1', null, '22013040950847000084', '2022-02-12 17:51:26', '22013040950847000084', '2022-02-12 17:51:26'), ('22021221095314000138', '新增', '新增', '22021220896714000138', 'F', null, '/system/menu/save', '1', '1', null, '22013040950847000084', '2022-02-12 17:51:33', '22013040950847000084', '2022-02-12 17:51:33'), ('22021221102773000138', '修改', '修改', '22021220896714000138', 'F', null, '/system/menu/update', '1', '1', null, '22013040950847000084', '2022-02-12 17:51:40', '22013040950847000084', '2022-02-12 17:51:40'), ('22021221109589000138', '删除', '删除', '22021220896714000138', 'F', null, '/system/menu/del', '1', '1', null, '22013040950847000084', '2022-02-12 17:51:47', '22013040950847000084', '2022-02-12 17:51:47'), ('22021221121678000138', '查询明细', '查询明细', '22021220896714000138', 'F', null, '/system/menu/single', '1', '1', null, '22013040950847000084', '2022-02-12 17:51:59', '22013040950847000084', '2022-02-12 17:51:59'), ('22021221133622000138', '新增权限', '新增权限', '22021220896714000138', 'F', null, '/system/menu/func/save', '1', '1', null, '22013040950847000084', '2022-02-12 17:52:11', '22013040950847000084', '2022-02-12 17:52:11'), ('22021221145219000138', '修改权限', '修改权限', '22021220896714000138', 'F', null, '/system/menu/func/update', '1', '1', null, '22013040950847000084', '2022-02-12 17:52:23', '22013040950847000084', '2022-02-12 17:52:23');
COMMIT;

-- ----------------------------
-- Table structure for `t_sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role`;
CREATE TABLE `t_sys_role` (
`role_id`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID' ,
`role_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称' ,
`remark`  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' ,
`create_user`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`create_time`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`update_user`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`update_time`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
PRIMARY KEY (`role_id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='角色'

;

-- ----------------------------
-- Records of t_sys_role
-- ----------------------------
BEGIN;
INSERT INTO `t_sys_role` VALUES ('22021240564408000138', '超级管理员', null, '22013040950847000084', '2022-02-12 23:16:02', '22013040950847000084', '2022-02-12 23:16:02');
COMMIT;

-- ----------------------------
-- Table structure for `t_sys_role_menu`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role_menu`;
CREATE TABLE `t_sys_role_menu` (
`role_id`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID' ,
`menu_id`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单ID' ,
PRIMARY KEY (`role_id`, `menu_id`),
FOREIGN KEY (`menu_id`) REFERENCES `t_sys_menu` (`menu_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `fk_rolemenu_ref_menu` (`menu_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='角色和菜单关联表'

;

-- ----------------------------
-- Records of t_sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `t_sys_role_menu` VALUES ('22021240564408000138', '22021220868051000138'), ('22021240564408000138', '22021220879115000138'), ('22021240564408000138', '22021220896714000138'), ('22021240564408000138', '22021220927630000138'), ('22021240564408000138', '22021220938979000138'), ('22021240564408000138', '22021220948854000138'), ('22021240564408000138', '22021220958161000138'), ('22021240564408000138', '22021220969051000138'), ('22021240564408000138', '22021220981781000138'), ('22021240564408000138', '22021220994555000138'), ('22021240564408000138', '22021221006012000138'), ('22021240564408000138', '22021221023125000138'), ('22021240564408000138', '22021221030073000138'), ('22021240564408000138', '22021221038308000138'), ('22021240564408000138', '22021221046144000138'), ('22021240564408000138', '22021221060910000138'), ('22021240564408000138', '22021221071941000138'), ('22021240564408000138', '22021221088773000138'), ('22021240564408000138', '22021221095314000138'), ('22021240564408000138', '22021221102773000138'), ('22021240564408000138', '22021221109589000138'), ('22021240564408000138', '22021221121678000138'), ('22021240564408000138', '22021221133622000138'), ('22021240564408000138', '22021221145219000138');
COMMIT;

-- ----------------------------
-- Table structure for `t_sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user`;
CREATE TABLE `t_sys_user` (
`user_id`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID' ,
`login_name`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号' ,
`user_type`  int(1) NULL DEFAULT 1 COMMENT '用户类型(1=系统用户；2=普通用户)' ,
`real_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '姓名' ,
`sex`  int(1) NULL DEFAULT 1 COMMENT '性别(1=男；0=女)' ,
`phone`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机' ,
`email`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱' ,
`password`  char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码' ,
`salt`  char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐密码' ,
`status`  int(1) NULL DEFAULT 0 COMMENT '账号状态(0=正常；1=冻结)' ,
`home_action`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认主页' ,
`login_ip`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后登录IP' ,
`login_time`  datetime NULL DEFAULT NULL COMMENT '最后登录时间' ,
`remark`  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' ,
`create_user`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`create_time`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`update_user`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`update_time`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
PRIMARY KEY (`user_id`),
INDEX `idx_login_name` (`login_name`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='用户'

;

-- ----------------------------
-- Records of t_sys_user
-- ----------------------------
BEGIN;
INSERT INTO `t_sys_user` VALUES ('22013040950847000084', 'admin', '1', '管理员', '1', null, null, '6D5B4A50E8A661FA33F4AB98929E7769', '1bb144ad623d426e8fcbe6537fcde2b4', '0', null, '127.0.0.1', '2022-02-12 23:23:57', 'ADMIN超管', 'TyCode', '2022-01-30 11:22:18', '22013040950847000084', '2022-02-12 23:23:57');
COMMIT;

-- ----------------------------
-- Table structure for `t_sys_user_menu`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_menu`;
CREATE TABLE `t_sys_user_menu` (
`user_id`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID' ,
`menu_id`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单ID' ,
PRIMARY KEY (`user_id`, `menu_id`),
FOREIGN KEY (`menu_id`) REFERENCES `t_sys_menu` (`menu_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `fk_usermenu_ref_menu` (`menu_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='用户和菜单关联表'

;

-- ----------------------------
-- Records of t_sys_user_menu
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `t_sys_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_role`;
CREATE TABLE `t_sys_user_role` (
`user_id`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID' ,
`role_id`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID' ,
PRIMARY KEY (`user_id`, `role_id`),
FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `fk_userole_ref_role` (`role_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='用户和角色关联表'

;

-- ----------------------------
-- Records of t_sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `t_sys_user_role` VALUES ('22013040950847000084', '22021240564408000138');
COMMIT;
