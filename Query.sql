-- Seed dữ liệu quản trị viên mặc định
INSERT INTO users (id, email, username, mat_khau, ho_ten, trang_thai, vai_tro_id) VALUES
                                                                                      ('ADMIN-001', 'admin1@travi.vn', 'admin.root', '$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK', 'Nguyen Quan Tri 1', 'HOAT_DONG', '5004952f-2316-4f04-87a2-524df239f812'),
                                                                                      ('ADMIN-002', 'admin2@travi.vn', 'admin.ops', '$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK', 'Tran Quan Tri 2', 'HOAT_DONG', '5004952f-2316-4f04-87a2-524df239f812'),
                                                                                      ('ADMIN-003', 'admin3@travi.vn', 'admin.audit', '$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK', 'Le Quan Tri 3', 'HOAT_DONG', '5004952f-2316-4f04-87a2-524df239f812');

INSERT INTO quan_tri_vien (id, cap_do_quyen) VALUES
                                                 ('ADMIN-001', 3),
                                                 ('ADMIN-002', 2),
                                                 ('ADMIN-003', 1);