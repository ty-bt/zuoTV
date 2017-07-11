/*
初始化platform平台表及数据
Navicat MySQL Data Transfer

Source Server Version : 50624

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2017-07-11 21:38:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for platform
-- ----------------------------
DROP TABLE IF EXISTS `platform`;
CREATE TABLE `platform` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `flag` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `last_updated` datetime NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `sort` bigint(20) NOT NULL,
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `on_line_ad` bigint(20) NOT NULL,
  `on_line_room` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `flag` (`flag`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of platform
-- ----------------------------
INSERT INTO `platform` VALUES ('1', '46449', '2016-07-12 16:32:07', 'douyu', '2017-07-11 21:37:59', '斗鱼', '0', 'http://www.douyu.com/', '27353433', '9463');
INSERT INTO `platform` VALUES ('2', '46741', '2016-07-13 16:50:12', 'panda', '2017-07-11 21:37:59', '熊猫', '0', 'http://www.panda.tv/', '7933373', '3488');
INSERT INTO `platform` VALUES ('3', '46485', '2016-11-08 16:50:12', 'quanMin', '2017-07-11 21:37:59', '全民', '0', 'http://www.quanmin.tv/', '15207103', '1228');
INSERT INTO `platform` VALUES ('4', '46271', '2016-11-08 16:58:18', 'zhanQi', '2017-07-11 21:37:59', '战旗', '0', 'https://www.zhanqi.tv/', '9323701', '927');
INSERT INTO `platform` VALUES ('5', '43732', '2016-11-08 17:22:35', 'huYa', '2017-07-11 21:37:59', '虎牙', '0', 'http://www.huya.com/', '12654808', '14484');
INSERT INTO `platform` VALUES ('6', '43540', '2016-11-08 17:55:37', 'longZhu', '2017-07-11 21:37:59', '龙珠', '0', 'http://longzhu.com/', '46349994', '2923');
INSERT INTO `platform` VALUES ('7', '10616', '2017-04-19 22:34:28', 'huoMao', '2017-06-06 12:07:57', '火猫', '0', 'https://www.huomao.com/', '203027', '101');
