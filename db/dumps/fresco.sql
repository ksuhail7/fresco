CREATE DATABASE  IF NOT EXISTS `fresco` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `fresco`;
-- MySQL dump 10.13  Distrib 5.7.16, for osx10.12 (x86_64)
--
-- Host: localhost    Database: fresco
-- ------------------------------------------------------
-- Server version	5.7.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ID_GEN`
--

DROP TABLE IF EXISTS `ID_GEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ID_GEN` (
  `gen_key` varchar(64) NOT NULL,
  `gen_value` int(11) NOT NULL DEFAULT '0',
  UNIQUE KEY `ID_GEN_gen_key_uindex` (`gen_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document` (
  `docref` int(11) NOT NULL,
  `docid` varchar(128) NOT NULL,
  `storeid` int(11) NOT NULL,
  `creation_date` datetime NOT NULL,
  `created_by` varchar(64) NOT NULL,
  `update_date` datetime NOT NULL,
  `updated_by` varchar(64) NOT NULL,
  `docid_sha1` varchar(40) NOT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`docref`),
  UNIQUE KEY `document_id_uindex` (`docref`),
  KEY `document_store_id_fk` (`storeid`),
  CONSTRAINT `document_store_id_fk` FOREIGN KEY (`storeid`) REFERENCES `store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `document_version`
--

DROP TABLE IF EXISTS `document_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document_version` (
  `docref` int(11) NOT NULL,
  `version` bigint(20) NOT NULL,
  `filename` varchar(128) NOT NULL,
  `filesize_in_bytes` bigint(20) DEFAULT NULL,
  `mimetype` varchar(64) DEFAULT NULL,
  `sha1_checksum` char(40) DEFAULT NULL,
  `creation_date` datetime NOT NULL,
  `created_by` varchar(64) NOT NULL,
  `update_date` datetime DEFAULT NULL,
  `updated_by` varchar(64) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  UNIQUE KEY `document_versions_id_uindex` (`docref`,`version`),
  CONSTRAINT `docref` FOREIGN KEY (`docref`) REFERENCES `document` (`docref`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `filesystem`
--

DROP TABLE IF EXISTS `filesystem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `filesystem` (
  `id` int(11) NOT NULL,
  `path` varchar(256) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `filesystem_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repo_fs_mapping`
--

DROP TABLE IF EXISTS `repo_fs_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repo_fs_mapping` (
  `repo_id` int(11) NOT NULL,
  `fs_id` int(11) NOT NULL,
  PRIMARY KEY (`repo_id`,`fs_id`),
  KEY `repo_fs_mapping_filesystem_id_fk` (`fs_id`),
  CONSTRAINT `repo_fs_mapping_filesystem_id_fk` FOREIGN KEY (`fs_id`) REFERENCES `filesystem` (`id`),
  CONSTRAINT `repo_fs_mapping_repository_id_fk` FOREIGN KEY (`repo_id`) REFERENCES `repository` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repository`
--

DROP TABLE IF EXISTS `repository`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repository` (
  `id` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creation_date` datetime NOT NULL,
  `created_by` varchar(32) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `update_date` datetime DEFAULT NULL,
  `updated_by` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `repository_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `store`
--

DROP TABLE IF EXISTS `store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store` (
  `id` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `repo_id` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_by` varchar(32) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `updated_by` varchar(45) NOT NULL,
  `update_date` datetime NOT NULL,
  `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `store_repository_id_fk` (`repo_id`),
  CONSTRAINT `store_repository_id_fk` FOREIGN KEY (`repo_id`) REFERENCES `repository` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'fresco'
--
/*!50003 DROP PROCEDURE IF EXISTS `create_document` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `create_document`(
IN docid varchar(128),
IN storeid int,
IN docid_sha1 char(40),
IN version long,
IN filename varchar(128),
IN filesize bigint,
IN mimetype varchar(128),
IN sha1cksum char(40),
IN requestor varchar(64),
IN is_active boolean,
OUT docref int
)
BEGIN
    declare creation_time datetime default now();
    declare docRefKey varchar(20) default 'document_ref';
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
    ROLLBACK;
    RESIGNAL;
    END;
    
	if is_active is null then
		select is_active = true;
	end if;
    
    -- if not exists (select 1 from document where docid = docid and storeid = storeid) then
		call gen_id('document_ref', docref);
	-- end if;
	
    
    start transaction;
    -- if not exists (select 1 from document where docid = docid and storeid = storeid) then
        insert into document
    (docref, docid, storeid, creation_date, created_by, update_date, updated_by, docid_sha1, is_active)
    values
    (docref, docid, storeid, creation_time, requestor,
    creation_time,
    requestor,
    docid_sha1,
    is_active);
    -- end if;
    
    insert into document_version
    (docref, version, filename, filesize_in_bytes, mimetype,
    sha1_checksum, creation_date, created_by, update_date, updated_by, is_active)
    values
    (docref,
    version, 
    filename,
    filesize,
    mimetype,
    sha1cksum,
    creation_time,
    requestor,
    creation_time,
    requestor,
    is_active);
    
    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `gen_id` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `gen_id`(IN parameter varchar(64), OUT value int)
BEGIN
if not exists (select 1 from ID_GEN where gen_key = parameter) then
	insert into ID_GEN (gen_key, gen_value) values (parameter, 0);
else
SELECT gen_value FROM ID_GEN where gen_key = parameter FOR UPDATE;
UPDATE ID_GEN SET gen_value = gen_value + 1 where gen_key = parameter;
end if;
select gen_value into value from ID_GEN where gen_key=parameter;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-07 21:02:22
