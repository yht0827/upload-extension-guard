-- 고정 확장자 초기 데이터
INSERT INTO fixed_extension (extension, blocked, created_at, updated_at) VALUES
    ('bat', false, NOW(), NOW()),
    ('cmd', false, NOW(), NOW()),
    ('com', false, NOW(), NOW()),
    ('cpl', false, NOW(), NOW()),
    ('exe', false, NOW(), NOW()),
    ('scr', false, NOW(), NOW()),
    ('js', false, NOW(), NOW());

-- 동시성 제어용 락 레코드
INSERT INTO extension_lock (id) VALUES ('LOCK');
