-- 初始化权限表数据
INSERT INTO permissions (permission_id, permission_code, description) VALUES
                                                                          (1, 'user:read', '允许查看基础信息'),
                                                                          (2, 'employee:read', '允许员工权限读'),
                                                                          (3, 'employee:write', '允许员工权限写'),
                                                                          (4, 'admin:all', '允许系统权限操作');

-- 初始化角色表数据
INSERT INTO roles (role_id, role_name, description) VALUES
                                                        (1, 'USER', '默认角色'),
                                                        (2, 'EMPLOYEE', '员工'),
                                                        (3, 'ADMIN', '管理员');

-- 初始化角色-权限关联数据
INSERT INTO role_permissions (role_id, permission_id) VALUES
                                                          (1, 1),
                                                          (3, 1),
                                                          (2, 2),
                                                          (3, 2),
                                                          (2, 3),
                                                          (3, 3),
                                                          (3, 4);