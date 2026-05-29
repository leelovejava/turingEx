ALTER TABLE `tz_user`
    ADD COLUMN `password` varchar(255) DEFAULT NULL COMMENT 'Plain login password' AFTER `login_password`;
