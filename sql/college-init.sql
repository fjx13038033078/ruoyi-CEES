-- =============================================
-- 高考志愿推荐系统 - 新增功能模块初始化脚本
-- =============================================

-- 1. 新增角色数据
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time, remark)
VALUES
(102, '考生', 'student', 5, '5', '0', '0', 'admin', NOW(), '考生角色'),
(103, '家长', 'parent', 6, '5', '0', '0', 'admin', NOW(), '家长角色'),
(104, '招生管理员', 'enrollment_admin', 7, '1', '0', '0', 'admin', NOW(), '招生管理员角色');

-- 2. 新建考生档案表
CREATE TABLE IF NOT EXISTS `cers_student_profile` (
  `profile_id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id`            BIGINT       NOT NULL COMMENT '关联 sys_user.user_id',
  `real_name`          VARCHAR(20)  NOT NULL COMMENT '真实姓名',
  `id_card`            VARCHAR(18)  DEFAULT NULL COMMENT '身份证号',
  `exam_number`        VARCHAR(30)  DEFAULT NULL COMMENT '考生号',
  `province`           VARCHAR(20)  DEFAULT NULL COMMENT '所在省份',
  `exam_year`          INT          DEFAULT NULL COMMENT '高考年份',
  `subject_type`       TINYINT      DEFAULT NULL COMMENT '科类(1-历史类 2-物理类)',
  `selected_subjects`  VARCHAR(50)  DEFAULT NULL COMMENT '选科组合,逗号分隔(如:物理,化学,生物)',
  `total_score`        INT          DEFAULT NULL COMMENT '高考总分',
  `score_chinese`      INT          DEFAULT NULL COMMENT '语文成绩',
  `score_math`         INT          DEFAULT NULL COMMENT '数学成绩',
  `score_english`      INT          DEFAULT NULL COMMENT '英语成绩',
  `score_primary`      INT          DEFAULT NULL COMMENT '首选科目成绩(物理/历史)',
  `score_secondary1`   INT          DEFAULT NULL COMMENT '再选科目1成绩',
  `score_secondary2`   INT          DEFAULT NULL COMMENT '再选科目2成绩',
  `sex`                CHAR(1)      DEFAULT '0' COMMENT '性别(0男 1女)',
  `color_blind`        TINYINT      DEFAULT 0 COMMENT '是否色盲(0否 1是)',
  `color_weak`         TINYINT      DEFAULT 0 COMMENT '是否色弱(0否 1是)',
  `verify_status`      TINYINT      DEFAULT 0 COMMENT '认证状态(0待审核 1已通过 2已驳回)',
  `create_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`profile_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  UNIQUE KEY `uk_exam_number` (`exam_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考生档案表';

-- 3. 新建模拟志愿表
CREATE TABLE IF NOT EXISTS `cers_volunteer_form` (
  `form_id`        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '志愿表ID',
  `user_id`        BIGINT      NOT NULL COMMENT '用户ID',
  `form_name`      VARCHAR(50) DEFAULT NULL COMMENT '方案名称(如:冲刺方案/稳妥方案)',
  `exam_year`      INT         DEFAULT NULL COMMENT '高考年份',
  `batch_type`     TINYINT     DEFAULT NULL COMMENT '批次类型(1提前批 2本科批 3专科批)',
  `status`         TINYINT     DEFAULT 0 COMMENT '状态(0草稿 1已校验 2已锁定)',
  `total_items`    INT         DEFAULT 0 COMMENT '志愿数量',
  `warning_count`  INT         DEFAULT 0 COMMENT '冲突/预警数',
  `create_time`    DATETIME    DEFAULT CURRENT_TIMESTAMP,
  `update_time`    DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`form_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模拟志愿表';

CREATE TABLE IF NOT EXISTS `cers_volunteer_item` (
  `item_id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '志愿明细ID',
  `form_id`          BIGINT      NOT NULL COMMENT '所属志愿表ID',
  `sort_order`       INT         NOT NULL COMMENT '志愿序号(第几志愿)',
  `university_id`    BIGINT      NOT NULL COMMENT '院校ID',
  `major_id_1`       BIGINT      DEFAULT NULL COMMENT '专业志愿1',
  `major_id_2`       BIGINT      DEFAULT NULL COMMENT '专业志愿2',
  `major_id_3`       BIGINT      DEFAULT NULL COMMENT '专业志愿3',
  `major_id_4`       BIGINT      DEFAULT NULL COMMENT '专业志愿4',
  `major_id_5`       BIGINT      DEFAULT NULL COMMENT '专业志愿5',
  `major_id_6`       BIGINT      DEFAULT NULL COMMENT '专业志愿6',
  `is_adjustment`    TINYINT     DEFAULT 1 COMMENT '是否服从专业调剂(0否 1是)',
  `check_status`     TINYINT     DEFAULT 0 COMMENT '校验状态(0未校验 1通过 2有预警 3不可填报)',
  `check_messages`   TEXT        DEFAULT NULL COMMENT '校验信息(JSON数组)',
  `create_time`      DATETIME    DEFAULT CURRENT_TIMESTAMP,
  `update_time`      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`item_id`),
  KEY `idx_form_id` (`form_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿明细表';

-- 4. 新建霍兰德测评相关表
CREATE TABLE IF NOT EXISTS `cers_holland_question` (
  `question_id`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `question_text` VARCHAR(500) NOT NULL COMMENT '题目内容',
  `option_a`      VARCHAR(200) NOT NULL COMMENT '选项A内容',
  `option_b`      VARCHAR(200) NOT NULL COMMENT '选项B内容',
  `dimension_a`   CHAR(1)      NOT NULL COMMENT '选项A对应维度(R/I/A/S/E/C)',
  `dimension_b`   CHAR(1)      NOT NULL COMMENT '选项B对应维度(R/I/A/S/E/C)',
  `sort_order`    INT          DEFAULT 0 COMMENT '题目顺序',
  `status`        TINYINT      DEFAULT 0 COMMENT '状态(0启用 1停用)',
  PRIMARY KEY (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='霍兰德测试题库';

CREATE TABLE IF NOT EXISTS `cers_holland_test_record` (
  `record_id`    BIGINT   NOT NULL AUTO_INCREMENT COMMENT '测试记录ID',
  `user_id`      BIGINT   NOT NULL COMMENT '用户ID',
  `status`       TINYINT  DEFAULT 0 COMMENT '状态(0进行中 1已完成)',
  `start_time`   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `finish_time`  DATETIME DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='霍兰德测试记录';

CREATE TABLE IF NOT EXISTS `cers_holland_answer` (
  `answer_id`       BIGINT  NOT NULL AUTO_INCREMENT COMMENT '答题ID',
  `record_id`       BIGINT  NOT NULL COMMENT '测试记录ID',
  `question_id`     BIGINT  NOT NULL COMMENT '题目ID',
  `selected_option` CHAR(1) NOT NULL COMMENT '选择的选项(A/B)',
  PRIMARY KEY (`answer_id`),
  UNIQUE KEY `uk_record_question` (`record_id`, `question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='霍兰德答题记录';

CREATE TABLE IF NOT EXISTS `cers_holland_result` (
  `result_id`    BIGINT      NOT NULL AUTO_INCREMENT COMMENT '结果ID',
  `record_id`    BIGINT      NOT NULL COMMENT '测试记录ID',
  `user_id`      BIGINT      NOT NULL COMMENT '用户ID',
  `r_score`      INT         DEFAULT 0 COMMENT '现实型得分(Realistic)',
  `i_score`      INT         DEFAULT 0 COMMENT '研究型得分(Investigative)',
  `a_score`      INT         DEFAULT 0 COMMENT '艺术型得分(Artistic)',
  `s_score`      INT         DEFAULT 0 COMMENT '社会型得分(Social)',
  `e_score`      INT         DEFAULT 0 COMMENT '企业型得分(Enterprising)',
  `c_score`      INT         DEFAULT 0 COMMENT '常规型得分(Conventional)',
  `holland_code` VARCHAR(3)  DEFAULT NULL COMMENT '霍兰德代码(取前3高维度,如RIA)',
  `personality_summary` TEXT DEFAULT NULL COMMENT '性格特征描述',
  `recommended_majors`  TEXT DEFAULT NULL COMMENT '推荐专业方向(JSON)',
  `create_time`  DATETIME    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`result_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='霍兰德测评结果';

CREATE TABLE IF NOT EXISTS `cers_holland_major_mapping` (
  `mapping_id`      BIGINT      NOT NULL AUTO_INCREMENT COMMENT '映射ID',
  `holland_code`    VARCHAR(3)  NOT NULL COMMENT '霍兰德代码(如RI, RIA等)',
  `major_direction` VARCHAR(50) NOT NULL COMMENT '专业方向名称',
  `major_keywords`  VARCHAR(200) DEFAULT NULL COMMENT '匹配关键词(用于和cers_major.major_name匹配)',
  `description`     VARCHAR(500) DEFAULT NULL COMMENT '方向说明',
  PRIMARY KEY (`mapping_id`),
  KEY `idx_holland_code` (`holland_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='霍兰德代码与专业方向映射';

-- 5. 新增菜单数据
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2000, '智能填报', 0, 5, 'volunteer', NULL, 'M', '0', '0', '', 'form', 'admin', NOW());

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2001, '模拟填报', 2000, 1, 'form', 'college/volunteer/index', 'C', '0', '0', 'college:volunteer:list', 'edit', 'admin', NOW());

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2002, '专业测评', 2000, 2, 'assessment', 'college/assessment/index', 'C', '0', '0', 'college:assessment:list', 'skill', 'admin', NOW());

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2003, '测评报告', 2000, 3, 'report', 'college/assessment/report', 'C', '0', '0', 'college:assessment:report', 'chart', 'admin', NOW());

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2010, '考生管理', 0, 6, 'student', NULL, 'M', '0', '0', '', 'peoples', 'admin', NOW());

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2011, '认证审核', 2010, 1, 'verify', 'college/student/verify', 'C', '0', '0', 'college:student:verify', 'peoples', 'admin', NOW());

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2012, '考生档案', 2010, 2, 'profile', 'college/student/profile', 'C', '0', '0', 'college:student:profile', 'user', 'admin', NOW());

-- 6. 菜单与角色关联
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(102, 2000), (102, 2001), (102, 2002), (102, 2003), (102, 2012),
(103, 2000), (103, 2001), (103, 2002), (103, 2003),
(104, 2010), (104, 2011), (104, 2012),
(101, 2000), (101, 2001), (101, 2002), (101, 2003);

-- 7. 霍兰德测试题库 (50题)
INSERT INTO `cers_holland_question` (`question_id`, `question_text`, `option_a`, `option_b`, `dimension_a`, `dimension_b`, `sort_order`, `status`) VALUES
(1,  '你更喜欢哪种活动？', '修理电器或机械设备', '阅读科学杂志或书籍', 'R', 'I', 1, 0),
(2,  '你更喜欢哪种活动？', '参加手工制作或DIY', '进行科学实验或研究', 'R', 'I', 2, 0),
(3,  '你更喜欢哪种方式度过周末？', '户外运动或体力劳动', '参观美术馆或欣赏艺术展', 'R', 'A', 3, 0),
(4,  '你更愿意从事哪类工作？', '操作精密仪器或工具', '为他人提供咨询和帮助', 'R', 'S', 4, 0),
(5,  '以下哪个更吸引你？', '制造或组装产品', '策划和组织一场活动', 'R', 'E', 5, 0),
(6,  '你更喜欢哪种任务？', '按照图纸制作模型', '整理和归纳数据资料', 'R', 'C', 6, 0),
(7,  '你更感兴趣的是？', '研究自然现象的原因', '创作诗歌、小说或音乐', 'I', 'A', 7, 0),
(8,  '你更愿意参加哪种活动？', '解决复杂的数学或逻辑难题', '组织社区志愿者活动', 'I', 'S', 8, 0),
(9,  '以下哪个职业更吸引你？', '科学研究员', '企业经理或总监', 'I', 'E', 9, 0),
(10, '你更喜欢哪种工作方式？', '独立思考和分析问题', '按照规范流程处理文件', 'I', 'C', 10, 0),
(11, '你更喜欢哪类活动？', '绘画、设计或摄影', '教别人学习新技能', 'A', 'S', 11, 0),
(12, '以下哪个更适合你？', '自由创作不受约束', '领导团队完成目标', 'A', 'E', 12, 0),
(13, '你更倾向于？', '用艺术形式表达想法', '制作详细的计划表或清单', 'A', 'C', 13, 0),
(14, '你更愿意做什么？', '帮助同学解决困难', '说服别人接受你的想法', 'S', 'E', 14, 0),
(15, '以下哪个更像你？', '善于倾听和安慰他人', '善于记录和整理信息', 'S', 'C', 15, 0),
(16, '你更喜欢哪种角色？', '项目的决策者和推动者', '项目的执行者和记录者', 'E', 'C', 16, 0),
(17, '你更愿意做哪件事？', '修理家中损坏的物品', '参加辩论赛或演讲比赛', 'R', 'E', 17, 0),
(18, '你更感兴趣的领域是？', '了解计算机硬件原理', '了解人类心理和行为', 'R', 'S', 18, 0),
(19, '如果有两门选修课,你选？', '木工或金工实践课', '绘画或音乐欣赏课', 'R', 'A', 19, 0),
(20, '你更享受哪种体验？', '亲手种植花草或蔬菜', '探索宇宙和物理定律', 'R', 'I', 20, 0),
(21, '你更愿意研究什么？', '新药物的化学成分', '新的市场营销策略', 'I', 'E', 21, 0),
(22, '以下哪个更吸引你？', '破解密码或谜题', '写一首歌或编一个故事', 'I', 'A', 22, 0),
(23, '你更喜欢哪种学习方式？', '做实验验证理论', '和同学讨论社会问题', 'I', 'S', 23, 0),
(24, '你更倾向于哪种工作？', '数据分析和统计', '财务报表和审计', 'I', 'C', 24, 0),
(25, '以下哪个更像你？', '喜欢设计房间的布局', '喜欢照顾和陪伴老人', 'A', 'S', 25, 0),
(26, '你更愿意做什么？', '设计一个网站或APP界面', '管理一个网店的运营', 'A', 'E', 26, 0),
(27, '以下哪种活动更吸引你？', '即兴表演或舞蹈', '使用Excel制作图表', 'A', 'C', 27, 0),
(28, '你更喜欢哪种社交方式？', '组织聚会帮助朋友认识新人', '主持会议推动项目进展', 'S', 'E', 28, 0),
(29, '你更擅长什么？', '理解他人的感受和需求', '管理自己的时间和任务', 'S', 'C', 29, 0),
(30, '以下哪个角色更适合你？', '创业公司的创始人', '政府部门的公务员', 'E', 'C', 30, 0),
(31, '你更愿意做什么？', '搭建一个机器人', '在实验室分析样本', 'R', 'I', 31, 0),
(32, '你更感兴趣的是？', '学习3D建模技术', '学习心理咨询技巧', 'R', 'S', 32, 0),
(33, '以下哪个更吸引你？', '参加汽车改装俱乐部', '参加文学写作俱乐部', 'R', 'A', 33, 0),
(34, '你更愿意做什么？', '探索海底世界的奥秘', '策划一场商业路演', 'I', 'E', 34, 0),
(35, '你更喜欢哪类书籍？', '科普类(如宇宙、基因)', '文艺类(如诗集、小说)', 'I', 'A', 35, 0),
(36, '以下哪种工作更适合你？', '医学研究', '社区服务', 'I', 'S', 36, 0),
(37, '你更倾向于哪种表达？', '用画笔或镜头记录生活', '用数字和图表描述趋势', 'A', 'C', 37, 0),
(38, '你更愿意参加什么？', '戏剧表演工作坊', '领导力培训营', 'A', 'E', 38, 0),
(39, '以下哪个更像你？', '乐于教导和分享知识', '擅长谈判和达成交易', 'S', 'E', 39, 0),
(40, '你更喜欢哪种工作环境？', '温馨的团队合作氛围', '高效有序的办公环境', 'S', 'C', 40, 0),
(41, '你更愿意做什么？', '野外考察收集标本', '编写程序解决问题', 'R', 'I', 41, 0),
(42, '以下哪种活动更吸引你？', '手工制作家具', '在台上进行艺术表演', 'R', 'A', 42, 0),
(43, '你更愿意做什么？', '安装家庭网络和监控', '为社区居民提供法律援助', 'R', 'S', 43, 0),
(44, '你更喜欢？', '分析基因测序的数据', '检查核对财务账目', 'I', 'C', 44, 0),
(45, '以下哪个职业更吸引你？', '天文学家', '心理治疗师', 'I', 'S', 45, 0),
(46, '你更愿意做什么？', '拍一部微电影', '管理一个学生社团', 'A', 'E', 46, 0),
(47, '你更喜欢的角色是？', '舞台导演', '档案管理员', 'A', 'C', 47, 0),
(48, '以下哪个更像你？', '经常帮助同学补习功课', '经常主动竞选班级职务', 'S', 'E', 48, 0),
(49, '你更倾向于哪种工作？', '客户服务与售后支持', '仓库管理与库存盘点', 'S', 'C', 49, 0),
(50, '你更愿意成为？', '一位成功的企业家', '一位出色的会计师', 'E', 'C', 50, 0);

-- 8. 霍兰德代码与专业方向映射数据
INSERT INTO `cers_holland_major_mapping` (`holland_code`, `major_direction`, `major_keywords`, `description`) VALUES
('R',   '工程技术', '机械,电气,自动化,土木,建筑,车辆,材料,能源', '适合动手能力强、喜欢操作工具和机械的人'),
('R',   '计算机硬件', '计算机,电子信息,通信,物联网,集成电路', '适合对硬件和电子系统感兴趣的人'),
('R',   '农林牧渔', '农学,林学,畜牧,水产,园艺,植物', '适合喜欢与自然打交道的人'),
('I',   '自然科学', '数学,物理,化学,生物,天文,地质,海洋', '适合具有探索精神和逻辑思维的人'),
('I',   '医学研究', '基础医学,临床医学,药学,生物医学,预防医学', '适合对生命科学感兴趣的研究型人才'),
('I',   '信息技术', '计算机科学,软件工程,人工智能,数据科学,网络', '适合喜欢用逻辑和算法解决问题的人'),
('A',   '艺术设计', '设计,美术,动画,数字媒体,视觉传达,环境设计', '适合有创造力和审美能力的人'),
('A',   '传媒影视', '新闻,传播,广播电视,编导,播音,广告', '适合善于表达和创意思维的人'),
('A',   '文学语言', '中文,外语,翻译,汉语言文学,比较文学', '适合对语言和文字有热情的人'),
('S',   '教育', '教育学,学前教育,小学教育,特殊教育,教育技术', '适合有耐心和热爱教育事业的人'),
('S',   '心理健康', '心理学,应用心理,社会工作,社会学', '适合善于理解他人、乐于助人的人'),
('S',   '医疗护理', '护理学,康复治疗,中医学,口腔医学', '适合细心、有爱心的人'),
('S',   '法学', '法学,知识产权,社会学,政治学', '适合关注社会公平正义的人'),
('E',   '工商管理', '工商管理,市场营销,人力资源,物流管理', '适合有领导力和决策能力的人'),
('E',   '金融经济', '金融,经济,国际贸易,投资,保险,财务', '适合对商业和金融敏感的人'),
('E',   '法律政治', '法学,行政管理,公共管理,国际关系', '适合善于沟通和说服他人的人'),
('C',   '财务会计', '会计,审计,财务管理,税务,统计', '适合细心、有条理的人'),
('C',   '行政管理', '行政管理,公共事业管理,档案学,图书情报', '适合做事有规划、注重细节的人'),
('C',   '信息管理', '信息管理,电子商务,大数据管理,供应链管理', '适合善于组织和处理信息的人'),
('RI',  '工程研发', '机械设计,电子工程,航空航天,船舶工程,核工程', '兼具动手能力和研究精神，适合技术研发'),
('RI',  '计算机工程', '计算机,软件,人工智能,机器人,自动化', '将实践操作与理论研究结合的工程人才'),
('IA',  '建筑设计', '建筑学,城乡规划,风景园林,室内设计', '兼具科学思维和艺术审美的创造型人才'),
('IS',  '医学临床', '临床医学,口腔医学,中医学,中西医结合', '兼具研究能力和服务精神的医学人才'),
('AS',  '教育艺术', '音乐教育,美术教育,舞蹈教育,艺术教育', '兼具艺术才能和教育热情的人'),
('SE',  '公共服务', '公共管理,社会工作,城市管理,社区管理', '兼具服务意识和管理能力的人'),
('EC',  '企业运营', '工商管理,财务管理,项目管理,运营管理', '兼具商业眼光和执行力的管理人才'),
('RE',  '工程管理', '工程管理,工程造价,建设管理', '兼具技术背景和管理能力的人'),
('IC',  '数据科学', '统计学,数据科学,精算,量化分析', '兼具分析思维和数据处理能力的人'),
('AE',  '创意商业', '广告学,文化产业管理,会展经济,传媒经济', '兼具创意思维和商业头脑的人'),
('SC',  '行政服务', '行政管理,人力资源管理,劳动关系', '兼具服务意识和条理性的管理人才');
