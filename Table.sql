CREATE DATABASE  IF NOT EXISTS `user_database` ;
USE `user_database`;


DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45),
  `last_name` varchar(45),
  `city` varchar(45),
  `state` varchar(45),
  `country` varchar(45),
  `username` varchar(45) ,
  `password` varchar(100),
  PRIMARY KEY (`id`)

) ;