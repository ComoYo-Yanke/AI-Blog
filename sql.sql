-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: blog
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `content` text NOT NULL,
  `summary` varchar(500) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `status` int NOT NULL DEFAULT '1' COMMENT '1=active, 0=disabled',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (1,'Hello World','This is my first blog post!','A test article',1,1,'2026-06-03 12:58:06','2026-06-03 19:28:07'),(2,'这是标题','内容','摘要',3,1,'2026-06-03 13:01:16','2026-06-03 13:01:16'),(3,'1','1','1',1,1,'2026-06-03 19:04:13','2026-06-03 19:04:13'),(4,'2','2','2',1,1,'2026-06-03 19:04:17','2026-06-03 19:04:17'),(5,'3','3','3',1,1,'2026-06-03 19:04:20','2026-06-03 19:04:20'),(6,'文章','这是内容','',1,1,'2026-06-03 19:10:05','2026-06-03 19:10:05'),(7,'1','1','1',1,1,'2026-06-03 19:12:35','2026-06-03 19:12:35'),(8,'2','2','2',1,1,'2026-06-03 19:13:46','2026-06-03 19:13:46'),(9,'2','2','2',1,1,'2026-06-03 19:13:49','2026-06-03 19:13:49'),(10,'3','3','3',1,1,'2026-06-03 19:13:54','2026-06-03 19:13:54'),(11,'4','4','4',1,1,'2026-06-03 19:13:58','2026-06-03 19:13:58');
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL,
  `user_id` bigint NOT NULL,
  `article_id` bigint NOT NULL,
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID，用于回复',
  `status` int NOT NULL DEFAULT '1' COMMENT '1=active, 0=disabled',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `article_id` (`article_id`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`),
  CONSTRAINT `comment_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,'好',1,1,NULL,1,'2026-06-03 13:00:27','2026-06-03 13:00:27'),(2,'不错',3,1,NULL,1,'2026-06-03 13:01:22','2026-06-03 13:01:22'),(3,'垃圾',1,2,NULL,1,'2026-06-03 13:02:52','2026-06-03 13:02:52'),(4,'1',1,5,NULL,1,'2026-06-03 19:04:25','2026-06-03 19:04:25'),(5,'1',1,5,NULL,1,'2026-06-03 19:04:26','2026-06-03 19:04:26'),(6,'1',1,5,NULL,1,'2026-06-03 19:04:27','2026-06-03 19:04:27'),(7,'1',1,5,NULL,1,'2026-06-03 19:04:28','2026-06-03 19:04:28'),(8,'1',1,5,NULL,1,'2026-06-03 19:04:28','2026-06-03 19:04:28'),(9,'1',1,5,NULL,1,'2026-06-03 19:04:29','2026-06-03 19:04:29'),(10,'1',1,7,NULL,1,'2026-06-03 19:12:43','2026-06-03 19:12:43'),(11,'1',1,7,NULL,1,'2026-06-03 19:12:44','2026-06-03 19:12:44'),(12,'1',1,7,NULL,1,'2026-06-03 19:12:45','2026-06-03 19:12:45'),(13,'1',1,7,NULL,1,'2026-06-03 19:12:45','2026-06-03 19:12:45'),(14,'1',1,7,NULL,1,'2026-06-03 19:12:46','2026-06-03 19:12:46'),(15,'1',1,7,NULL,1,'2026-06-03 19:12:46','2026-06-03 19:12:46'),(16,'1',1,7,NULL,1,'2026-06-03 19:12:47','2026-06-03 19:12:47'),(17,'1',1,1,NULL,1,'2026-06-03 19:14:03','2026-06-03 19:14:03'),(18,'2',1,1,NULL,1,'2026-06-03 19:14:04','2026-06-03 19:14:04'),(19,'3',1,1,NULL,1,'2026-06-03 19:14:04','2026-06-03 19:14:04'),(20,'4',1,1,NULL,1,'2026-06-03 19:14:06','2026-06-03 19:14:06'),(21,'5',1,1,NULL,1,'2026-06-03 19:14:06','2026-06-03 19:14:06'),(22,'123',3,2,3,1,'2026-06-03 19:30:40','2026-06-03 19:30:40');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow`
--

DROP TABLE IF EXISTS `follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `follower_id` bigint NOT NULL COMMENT '关注者',
  `following_id` bigint NOT NULL COMMENT '被关注者',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follow` (`follower_id`,`following_id`),
  KEY `following_id` (`following_id`),
  CONSTRAINT `follow_ibfk_1` FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`),
  CONSTRAINT `follow_ibfk_2` FOREIGN KEY (`following_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow`
--

LOCK TABLES `follow` WRITE;
/*!40000 ALTER TABLE `follow` DISABLE KEYS */;
INSERT INTO `follow` VALUES (3,3,1,'2026-06-03 19:31:24'),(4,4,3,'2026-06-03 20:43:11');
/*!40000 ALTER TABLE `follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'USER',
  `status` int NOT NULL DEFAULT '1' COMMENT '1=active, 0=disabled',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2a$10$uQeWHmlv7Um0tyFBx48FMup0/nMcJIv49kKEfekGEsjcYuuF3hKqy','admin@blog.com','ADMIN',1,'2026-06-03 12:55:08','2026-06-03 13:00:10'),(2,'user1','$2a$10$0DHm6JDykQXde1dX4MPHhu5FQgM3FQ0WO4hv8AVG5uvU9icbcH4CG','user1@test.com','USER',1,'2026-06-03 12:58:06','2026-06-03 12:58:06'),(3,'user','$2a$10$c.WqarewvYVIV0apca71ZeX/M2MMG.sfh97rk64KZIJj0TqhrAFee','123@qq.com','USER',1,'2026-06-03 13:00:47','2026-06-03 13:00:47'),(4,'张无忌','$2a$10$d6FisqWBRnw/wuRhp6wt5eCDZG65V4A7OquC5Q4wbToja.7vgeZpy','123','USER',1,'2026-06-03 20:42:50','2026-06-03 20:42:50');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-04 16:52:33
