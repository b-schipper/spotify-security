DROP TABLE IF EXISTS 'roles';
CREATE TABLE 'roles' (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(20) NOT NULL,
    PRIMARY KEY ('id')
) ENGINE=InnoDB;

INSERT INTO `roles` VALUES (1, 'ROLE_USER'),(2, 'ROLE_ARTIST'),(3, 'ROLE_ADMIN');