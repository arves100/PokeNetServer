/*
Navicat MySQL Data Transfer

Source Server         : MySQL
Source Server Version : 50718
Source Host           : localhost:3310
Source Database       : pokenet

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-06-13 13:45:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pn_bag
-- ----------------------------
DROP TABLE IF EXISTS `pn_bag`;
CREATE TABLE `pn_bag` (
  `member` int(11) NOT NULL,
  `item` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  UNIQUE KEY `memberitem` (`member`,`item`),
  KEY `Memberid` (`member`)
) ENGINE=MyISAM DEFAULT CHARSET=ascii;

-- ----------------------------
-- Table structure for pn_box
-- ----------------------------
DROP TABLE IF EXISTS `pn_box`;
CREATE TABLE `pn_box` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member` int(11) DEFAULT NULL,
  `pokemon0` int(11) DEFAULT NULL,
  `pokemon1` int(11) DEFAULT NULL,
  `pokemon2` int(11) DEFAULT NULL,
  `pokemon3` int(11) DEFAULT NULL,
  `pokemon4` int(11) DEFAULT NULL,
  `pokemon5` int(11) DEFAULT NULL,
  `pokemon6` int(11) DEFAULT NULL,
  `pokemon7` int(11) DEFAULT NULL,
  `pokemon8` int(11) DEFAULT NULL,
  `pokemon9` int(11) DEFAULT NULL,
  `pokemon10` int(11) DEFAULT NULL,
  `pokemon11` int(11) DEFAULT NULL,
  `pokemon12` int(11) DEFAULT NULL,
  `pokemon13` int(11) DEFAULT NULL,
  `pokemon14` int(11) DEFAULT NULL,
  `pokemon15` int(11) DEFAULT NULL,
  `pokemon16` int(11) DEFAULT NULL,
  `pokemon17` int(11) DEFAULT NULL,
  `pokemon18` int(11) DEFAULT NULL,
  `pokemon19` int(11) DEFAULT NULL,
  `pokemon20` int(11) DEFAULT NULL,
  `pokemon21` int(11) DEFAULT NULL,
  `pokemon22` int(11) DEFAULT NULL,
  `pokemon23` int(11) DEFAULT NULL,
  `pokemon24` int(11) DEFAULT NULL,
  `pokemon25` int(11) DEFAULT NULL,
  `pokemon26` int(11) DEFAULT NULL,
  `pokemon27` int(11) DEFAULT NULL,
  `pokemon28` int(11) DEFAULT NULL,
  `pokemon29` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for pn_members
-- ----------------------------
DROP TABLE IF EXISTS `pn_members`;
CREATE TABLE `pn_members` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(12) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `dob` varchar(12) DEFAULT NULL,
  `email` varchar(32) DEFAULT NULL,
  `lastLoginTime` varchar(42) DEFAULT NULL,
  `lastLoginServer` varchar(24) DEFAULT NULL,
  `lastLoginIP` varchar(16) DEFAULT NULL,
  `sprite` int(11) DEFAULT NULL,
  `pokemons` int(11) DEFAULT NULL,
  `money` int(11) DEFAULT NULL,
  `npcMul` varchar(24) DEFAULT NULL,
  `skHerb` int(11) DEFAULT NULL,
  `skCraft` int(11) DEFAULT NULL,
  `skFish` int(11) DEFAULT NULL,
  `skTrain` int(11) DEFAULT NULL,
  `skCoord` int(11) DEFAULT NULL,
  `skBreed` int(11) DEFAULT NULL,
  `x` int(11) DEFAULT NULL,
  `y` int(11) DEFAULT NULL,
  `mapX` int(11) DEFAULT NULL,
  `mapY` int(11) DEFAULT NULL,
  `bag` int(11) DEFAULT NULL,
  `badges` varchar(50) DEFAULT NULL,
  `healX` int(11) DEFAULT NULL,
  `healY` int(11) DEFAULT NULL,
  `healMapX` int(11) DEFAULT NULL,
  `healMapY` int(11) DEFAULT NULL,
  `isSurfing` varchar(5) DEFAULT NULL,
  `adminLevel` int(11) DEFAULT NULL,
  `muted` varchar(5) DEFAULT '0',
  `party` int(11) DEFAULT '-1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for pn_mypokes
-- ----------------------------
DROP TABLE IF EXISTS `pn_mypokes`;
CREATE TABLE `pn_mypokes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member` int(11) DEFAULT NULL,
  `party` int(11) DEFAULT NULL,
  `box0` int(11) DEFAULT NULL,
  `box1` int(11) DEFAULT NULL,
  `box2` int(11) DEFAULT NULL,
  `box3` int(11) DEFAULT NULL,
  `box4` int(11) DEFAULT NULL,
  `box5` int(11) DEFAULT NULL,
  `box6` int(11) DEFAULT NULL,
  `box7` int(11) DEFAULT NULL,
  `box8` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for pn_party
-- ----------------------------
DROP TABLE IF EXISTS `pn_party`;
CREATE TABLE `pn_party` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member` int(11) DEFAULT NULL,
  `pokemon0` int(11) DEFAULT NULL,
  `pokemon1` int(11) DEFAULT NULL,
  `pokemon2` int(11) DEFAULT NULL,
  `pokemon3` int(11) DEFAULT NULL,
  `pokemon4` int(11) DEFAULT NULL,
  `pokemon5` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for pn_pokemon
-- ----------------------------
DROP TABLE IF EXISTS `pn_pokemon`;
CREATE TABLE `pn_pokemon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(24) DEFAULT NULL,
  `speciesName` varchar(32) DEFAULT NULL,
  `exp` varchar(32) DEFAULT NULL,
  `baseExp` int(11) DEFAULT NULL,
  `expType` varchar(16) DEFAULT NULL,
  `isFainted` varchar(5) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `happiness` int(11) DEFAULT NULL,
  `gender` int(11) DEFAULT NULL,
  `nature` varchar(24) DEFAULT NULL,
  `abilityName` varchar(24) DEFAULT NULL,
  `itemName` varchar(28) DEFAULT NULL,
  `isShiny` varchar(5) DEFAULT NULL,
  `originalTrainerName` varchar(12) DEFAULT NULL,
  `move0` varchar(32) DEFAULT NULL,
  `move1` varchar(32) DEFAULT NULL,
  `move2` varchar(32) DEFAULT NULL,
  `move3` varchar(32) DEFAULT NULL,
  `hp` int(11) DEFAULT NULL,
  `atk` int(11) DEFAULT NULL,
  `def` int(11) DEFAULT NULL,
  `speed` int(11) DEFAULT NULL,
  `spATK` int(11) DEFAULT NULL,
  `spDEF` int(11) DEFAULT NULL,
  `evHP` int(11) DEFAULT NULL,
  `evATK` int(11) DEFAULT NULL,
  `evDEF` int(11) DEFAULT NULL,
  `evSPD` int(11) DEFAULT NULL,
  `evSPATK` int(11) DEFAULT NULL,
  `evSPDEF` int(11) DEFAULT NULL,
  `ivHP` int(11) DEFAULT NULL,
  `ivATK` int(11) DEFAULT NULL,
  `ivDEF` int(11) DEFAULT NULL,
  `ivSPD` int(11) DEFAULT NULL,
  `ivSPATK` int(11) DEFAULT NULL,
  `ivSPDEF` int(11) DEFAULT NULL,
  `pp0` int(11) DEFAULT NULL,
  `pp1` int(11) DEFAULT NULL,
  `pp2` int(11) DEFAULT NULL,
  `pp3` int(11) DEFAULT NULL,
  `maxpp0` int(11) DEFAULT NULL,
  `maxpp1` int(11) DEFAULT NULL,
  `maxpp2` int(11) DEFAULT NULL,
  `maxpp3` int(11) DEFAULT NULL,
  `ppUp0` int(11) DEFAULT NULL,
  `ppUp1` int(11) DEFAULT NULL,
  `ppUp2` int(11) DEFAULT NULL,
  `ppUp3` int(11) DEFAULT NULL,
  `date` varchar(28) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
