-- =====================================================
-- Hospital Information System (HIS) Test Data
-- Generated based on Java Entity Classes
-- =====================================================

-- =====================================================
-- Basic Data
-- =====================================================

-- Constant Types
INSERT INTO `constant_type` (`constant_type_id`, `constant_type_code`, `constant_type_name`) VALUES
(1, 'DEPT_CATEGORY', '科室类别'),
(2, 'USER_TITLE', '用户职称'),
(3, 'DRUGS_DOSAGE', '药品剂型'),
(4, 'DRUGS_TYPE', '药品类型'),
(5, 'PAY_MODE', '支付方式'),
(6, 'GENDER', '性别');

-- Constant Items
INSERT INTO `constant_items` (`constant_items_id`, `constant_type_id`, `constant_items_code`, `constant_items_name`) VALUES
-- 科室类别
(1, 1, 'INTERNAL', '内科'),
(2, 1, 'SURGERY', '外科'),
(3, 1, 'PEDIATRICS', '儿科'),
(4, 1, 'OBSTETRICS', '妇产科'),
(5, 1, 'EMERGENCY', '急诊科'),
(6, 1, 'TECH', '医技科室'),
-- 用户职称
(7, 2, 'CHIEF', '主任医师'),
(8, 2, 'ASSOCIATE_CHIEF', '副主任医师'),
(9, 2, 'ATTENDING', '主治医师'),
(10, 2, 'RESIDENT', '住院医师'),
(11, 2, 'NURSE', '护士'),
(12, 2, 'NURSE_HEAD', '护士长'),
-- 药品剂型
(13, 3, 'TABLET', '片剂'),
(14, 3, 'CAPSULE', '胶囊'),
(15, 3, 'INJECTION', '注射剂'),
(16, 3, 'SYRUP', '糖浆'),
(17, 3, 'OINTMENT', '软膏'),
-- 药品类型
(18, 4, 'WESTERN', '西药'),
(19, 4, 'CHINESE', '中药'),
(20, 4, 'PATENT', '中成药'),
-- 支付方式
(21, 5, 'CASH', '现金'),
(22, 5, 'CARD', '银行卡'),
(23, 5, 'WECHAT', '微信'),
(24, 5, 'ALIPAY', '支付宝'),
-- 性别
(25, 6, 'MALE', '男'),
(26, 6, 'FEMALE', '女');

-- Roles
INSERT INTO `role` (`role_id`, `function_id`, `role_name`) VALUES
(1, NULL, '系统管理员'),
(2, NULL, '挂号员'),
(3, NULL, '门诊医生'),
(4, NULL, '医技医生'),
(5, NULL, '药房管理员'),
(6, NULL, '收费员');

-- Functions
INSERT INTO `function` (`function_id`, `role_id`, `function_url`, `function_name`, `reverse1`) VALUES
(1, 1, '/admin/**', '系统管理', 1),
(2, 2, '/registration/**', '挂号管理', 1),
(3, 3, '/doctor/**', '门诊医生工作站', 1),
(4, 4, '/tech/**', '医技工作站', 1),
(5, 5, '/drug/**', '药房管理', 1),
(6, 6, '/expense/**', '收费管理', 1);

-- Departments
INSERT INTO `department` (`department_id`, `department_code`, `department_name`, `department_category_id`, `department_type`) VALUES
(1, 'D001', '内科门诊', 1, 'CLINICAL'),
(2, 'D002', '外科门诊', 2, 'CLINICAL'),
(3, 'D003', '儿科门诊', 3, 'CLINICAL'),
(4, 'D004', '妇产科门诊', 4, 'CLINICAL'),
(5, 'D005', '急诊科', 5, 'CLINICAL'),
(6, 'D006', '检验科', 6, 'TECH'),
(7, 'D007', '放射科', 6, 'TECH'),
(8, 'D008', '超声科', 6, 'TECH'),
(9, 'D009', '药房', 6, 'TECH'),
(10, 'D010', '收费处', 6, 'TECH');

-- Users
INSERT INTO `user` (`user_id`, `user_loginname`, `user_password`, `role_id`, `user_name`, `department_id`, `user_title_id`, `user_gender`, `user_status`, `user_scheduling_limitcount`) VALUES
(1, 'admin', 'admin123', 1, '系统管理员', NULL, NULL, '男', 'ACTIVE', 0),
(2, 'doctor001', '123456', 3, '张医生', 1, 7, '男', 'ACTIVE', 50),
(3, 'doctor002', '123456', 3, '李医生', 1, 8, '女', 'ACTIVE', 50),
(4, 'doctor003', '123456', 3, '王医生', 2, 9, '男', 'ACTIVE', 50),
(5, 'doctor004', '123456', 3, '赵医生', 3, 9, '女', 'ACTIVE', 50),
(6, 'doctor005', '123456', 3, '刘医生', 4, 8, '男', 'ACTIVE', 50),
(7, 'doctor006', '123456', 4, '孙医生', 6, 9, '男', 'ACTIVE', 0),
(8, 'doctor007', '123456', 4, '周医生', 7, 9, '女', 'ACTIVE', 0),
(9, 'nurse001', '123456', 5, '吴护士', 9, 11, '女', 'ACTIVE', 0),
(10, 'cashier001', '123456', 6, '郑收费员', 10, NULL, '男', 'ACTIVE', 0),
(11, 'reg001', '123456', 2, '钱挂号员', 10, NULL, '女', 'ACTIVE', 0);

-- Registration Levels
INSERT INTO `registration_level` (`registration_level_id`, `registration_level_name`, `is_default`, `registration_sequence`, `registration_cost`) VALUES
(1, '普通号', 'Y', 1, 10.00),
(2, '副主任医师号', 'N', 2, 20.00),
(3, '主任医师号', 'N', 3, 50.00),
(4, '急诊号', 'N', 4, 30.00),
(5, '专家号', 'N', 5, 100.00);

-- Calculation Types
INSERT INTO `calculation_type` (`calculation_type_id`, `calculation_type_name`) VALUES
(1, '自费'),
(2, '医保'),
(3, '公费'),
(4, '新农合');

-- Expense Types
INSERT INTO `expense_type` (`expense_type_id`, `expense_type_code`, `expense_type_name`) VALUES
(1, 'GH', '挂号费'),
(2, 'YF', '药费'),
(3, 'JC', '检查费'),
(4, 'CZ', '处置费'),
(5, 'CL', '材料费'),
(6, 'QT', '其他');

-- Disease Folders
INSERT INTO `disease_folder` (`disease_folder_id`, `disease_folder_name`) VALUES
(1, '常见病'),
(2, '慢性病'),
(3, '传染病'),
(4, '肿瘤');

-- Disease Types
INSERT INTO `disease_type` (`disease_type_id`, `disease_type_code`, `disease_type_name`, `disease_type_sequence`, `disease_type_type`, `disease_folder_id`) VALUES
(1, 'A00', '肠道传染病', 1, 'INFECTIOUS', 3),
(2, 'A01', '呼吸道传染病', 2, 'INFECTIOUS', 3),
(3, 'I00', '循环系统疾病', 3, 'CHRONIC', 2),
(4, 'J00', '呼吸系统疾病', 4, 'COMMON', 1),
(5, 'K00', '消化系统疾病', 5, 'COMMON', 1),
(6, 'N00', '泌尿生殖系统疾病', 6, 'COMMON', 1),
(7, 'C00', '恶性肿瘤', 7, 'TUMOR', 4);

-- Diseases
INSERT INTO `disease` (`disease_id`, `disease_code`, `disease_name`, `disease_icd`, `disease_type_id`, `disease_customize_name1`, `disease_customize_name2`) VALUES
(1, 'J00.0', '急性上呼吸道感染', 'J00.0', 4, '感冒', '上感'),
(2, 'J18.0', '支气管肺炎', 'J18.0', 4, '肺炎', NULL),
(3, 'I10', '原发性高血压', 'I10', 3, '高血压', NULL),
(4, 'I20', '心绞痛', 'I20', 3, '冠心病', NULL),
(5, 'K29', '胃炎', 'K29', 5, '胃病', NULL),
(6, 'K76.0', '脂肪肝', 'K76.0', 5, '脂肪肝', NULL),
(7, 'N39.0', '尿路感染', 'N39.0', 6, '尿感', NULL),
(8, 'A09', '急性胃肠炎', 'A09', 1, '急性肠胃炎', NULL),
(9, 'J06.9', '急性咽炎', 'J06.9', 4, '咽炎', NULL),
(10, 'J00.1', '流行性感冒', 'J00.1', 2, '流感', NULL);

-- Drugs
INSERT INTO `drugs` (`drugs_id`, `drugs_code`, `drugs_name`, `drugs_format`, `drugs_unit`, `drugs_manufacturer`, `drugs_dosage_id`, `drugs_type_id`, `drugs_price`, `drugs_mnemoniccode`, `create_time`, `reverse1`, `reverse2`, `reverse3`) VALUES
(1, 'D001', '阿莫西林胶囊', '0.25g*24粒', '盒', '华北制药', 14, 18, 15.50, 'AMXL', NOW(), NULL, NULL, NULL),
(2, 'D002', '头孢克肟分散片', '0.1g*6片', '盒', '广州白云山', 13, 18, 28.00, 'TBKF', NOW(), NULL, NULL, NULL),
(3, 'D003', '布洛芬缓释胶囊', '0.3g*20粒', '盒', '中美史克', 14, 18, 18.50, 'BLF', NOW(), NULL, NULL, NULL),
(4, 'D004', '阿司匹林肠溶片', '100mg*30片', '盒', '拜耳医药', 13, 18, 35.00, 'ASPL', NOW(), NULL, NULL, NULL),
(5, 'D005', '奥美拉唑肠溶胶囊', '20mg*14粒', '盒', '阿斯利康', 14, 18, 45.00, 'AMLZ', NOW(), NULL, NULL, NULL),
(6, 'D006', '复方甘草片', '100片', '瓶', '上海医药', 13, 19, 8.00, 'FGGC', NOW(), NULL, NULL, NULL),
(7, 'D007', '感冒清热颗粒', '12g*10袋', '盒', '同仁堂', 16, 20, 12.00, 'GMQR', NOW(), NULL, NULL, NULL),
(8, 'D008', '板蓝根颗粒', '10g*20袋', '盒', '白云山', 16, 20, 15.00, 'BLG', NOW(), NULL, NULL, NULL),
(9, 'D009', '盐酸氨溴索口服液', '100ml', '瓶', '勃林格殷格翰', 16, 18, 32.00, 'YSABS', NOW(), NULL, NULL, NULL),
(10, 'D010', '维生素C片', '100mg*100片', '瓶', '东北制药', 13, 18, 5.00, 'WSSC', NOW(), NULL, NULL, NULL),
(11, 'D011', '硝苯地平缓释片', '30mg*14片', '盒', '拜耳医药', 13, 18, 42.00, 'XBDP', NOW(), NULL, NULL, NULL),
(12, 'D012', '盐酸二甲双胍片', '0.5g*60片', '瓶', '中美施贵宝', 13, 18, 25.00, 'EJSG', NOW(), NULL, NULL, NULL);

-- Fmedical Items
INSERT INTO `fmedical_items` (`fmedical_items_id`, `fmedical_items_code`, `fmedical_items_name`, `fmedical_items_format`, `fmedical_items_price`, `expense_type_id`, `department_id`, `fmedical_items_mnemoniccode`, `fmedical_items_type`, `create_time`, `reverse1`, `reverse2`, `reverse3`) VALUES
(1, 'F001', '血常规', '五分类', 25.00, 3, 6, 'XCG', 'EXAMINATION', NOW(), NULL, NULL, NULL),
(2, 'F002', '尿常规', '干化学法', 15.00, 3, 6, 'NCG', 'EXAMINATION', NOW(), NULL, NULL, NULL),
(3, 'F003', '肝功能', '全套', 80.00, 3, 6, 'GGN', 'EXAMINATION', NOW(), NULL, NULL, NULL),
(4, 'F004', '肾功能', '全套', 60.00, 3, 6, 'SGN', 'EXAMINATION', NOW(), NULL, NULL, NULL),
(5, 'F005', '血糖', '空腹', 10.00, 3, 6, 'XT', 'EXAMINATION', NOW(), NULL, NULL, NULL),
(6, 'F006', '血脂', '全套', 100.00, 3, 6, 'XZ', 'EXAMINATION', NOW(), NULL, NULL, NULL),
(7, 'F007', '胸部X光', '正位片', 50.00, 3, 7, 'XBXG', 'EXAMINATION', NOW(), NULL, NULL, NULL),
(8, 'F008', '腹部B超', '常规', 120.00, 3, 8, 'FQBC', 'EXAMINATION', NOW(), NULL, NULL, NULL),
(9, 'F009', '心电图', '常规', 30.00, 3, 8, 'XDT', 'EXAMINATION', NOW(), NULL, NULL, NULL),
(10, 'F010', '换药', '小', 20.00, 4, 2, 'HY', 'TREATMENT', NOW(), NULL, NULL, NULL),
(11, 'F011', '清创缝合', '小', 100.00, 4, 2, 'QCFH', 'TREATMENT', NOW(), NULL, NULL, NULL),
(12, 'F012', '雾化吸入', '每次', 15.00, 4, 1, 'WHXR', 'TREATMENT', NOW(), NULL, NULL, NULL);

-- =====================================================
-- Patients
-- =====================================================

INSERT INTO `patient` (`patient_id`, `patient_name`, `patient_gender`, `patient_birth`, `patient_age`, `patient_identity`, `patient_address`) VALUES
(1, '张三', '男', '1985-05-15', 39, '110101198505150011', '北京市朝阳区建国路88号'),
(2, '李四', '女', '1990-08-20', 34, '110101199008200022', '北京市海淀区中关村大街1号'),
(3, '王五', '男', '1978-12-10', 46, '110101197812100033', '北京市西城区金融街10号'),
(4, '赵六', '女', '2000-03-25', 24, '110101200003250044', '北京市东城区王府井大街100号'),
(5, '钱七', '男', '1965-07-08', 59, '110101196507080055', '北京市丰台区丰台路50号'),
(6, '孙八', '女', '2015-11-30', 9, '110101201511300066', '北京市昌平区回龙观东大街'),
(7, '周九', '男', '1982-02-14', 42, '110101198202140077', '北京市通州区新华大街'),
(8, '吴十', '女', '1995-06-18', 29, '110101199506180088', '北京市大兴区黄村西大街');

-- =====================================================
-- Scheduling Rules
-- =====================================================

INSERT INTO `scheduling_rule` (`scheduling_rule_id`, `doctor_id`, `scheduling_rule_noonbreak`, `scheduling_rule_starttime`, `scheduling_rule_endtime`, `scheduling_rule_weekday`, `scheduling_rule_limitcount`) VALUES
(1, 2, 'AM', '08:00:00', '12:00:00', '1,2,3,4,5', 25),
(2, 2, 'PM', '14:00:00', '17:30:00', '1,2,3,4,5', 25),
(3, 3, 'AM', '08:00:00', '12:00:00', '1,2,3,4,5', 25),
(4, 3, 'PM', '14:00:00', '17:30:00', '1,2,3,4,5', 25),
(5, 4, 'AM', '08:00:00', '12:00:00', '1,3,5', 20),
(6, 4, 'PM', '14:00:00', '17:30:00', '2,4', 20),
(7, 5, 'AM', '08:00:00', '12:00:00', '1,2,3,4,5', 30),
(8, 6, 'AM', '08:00:00', '12:00:00', '1,2,3,4,5', 20);

-- =====================================================
-- Scheduling Info (for current week)
-- =====================================================

INSERT INTO `scheduling_info` (`scheduling_info_id`, `doctor_id`, `scheduling_noonbreak`, `scheduling_starttime`, `scheduling_endtime`, `scheduling_weekday`, `scheduling_limitcount`, `scheduling_restcount`) VALUES
(1, 2, 'AM', CONCAT(CURDATE(), ' 08:00:00'), CONCAT(CURDATE(), ' 12:00:00'), '1', 25, 20),
(2, 2, 'PM', CONCAT(CURDATE(), ' 14:00:00'), CONCAT(CURDATE(), ' 17:30:00'), '1', 25, 22),
(3, 3, 'AM', CONCAT(CURDATE(), ' 08:00:00'), CONCAT(CURDATE(), ' 12:00:00'), '1', 25, 18),
(4, 4, 'AM', CONCAT(CURDATE(), ' 08:00:00'), CONCAT(CURDATE(), ' 12:00:00'), '1', 20, 15),
(5, 5, 'AM', CONCAT(CURDATE(), ' 08:00:00'), CONCAT(CURDATE(), ' 12:00:00'), '1', 30, 25);

-- =====================================================
-- Medical Records and Registrations
-- =====================================================

-- Medical Records
INSERT INTO `medical_record` (`medical_record_id`, `doctor_id`, `is_treament_over`, `first_diagnosis_doctor_id`, `final_diagnosis_doctor_id`, `first_diagnosis_time`, `final_diagnosis_time`) VALUES
(1, 2, 'N', 2, NULL, NOW(), NULL),
(2, 2, 'Y', 2, 2, NOW() - INTERVAL 1 DAY, NOW()),
(3, 3, 'N', 3, NULL, NOW(), NULL),
(4, 4, 'Y', 4, 4, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY),
(5, 5, 'N', 5, NULL, NOW(), NULL);

-- Medical Record Home Pages
INSERT INTO `medical_record_home_page` (`medical_record_home_page_id`, `medical_record_id`, `doctor_id`, `chief_complaint`, `present_history`, `present_treatment`, `past_history`, `allergic_history`, `physical_examination`, `assistant_examination`) VALUES
(1, 1, 2, '发热、咳嗽3天', '患者3天前受凉后出现发热，体温最高38.5℃，伴咳嗽、咳痰，痰为白色粘痰。', NULL, '体健', '无', '咽部充血，双肺呼吸音粗', '血常规：白细胞升高'),
(2, 2, 2, '头晕、头痛1周', '患者1周前无明显诱因出现头晕、头痛，伴胸闷。', NULL, '高血压病史5年', '无', '血压160/100mmHg', '心电图：窦性心律'),
(3, 3, 3, '腹痛、腹泻2天', '患者2天前进食不洁食物后出现腹痛、腹泻，每日4-5次。', NULL, '体健', '无', '腹软，脐周压痛', '血常规：白细胞升高'),
(4, 4, 4, '右下腹痛1天', '患者1天前出现右下腹痛，伴恶心、呕吐。', NULL, '体健', '无', '右下腹压痛、反跳痛', '血常规：白细胞升高'),
(5, 5, 5, '咳嗽、喘息2天', '患儿2天前出现咳嗽、喘息，伴流涕。', NULL, '体健', '无', '双肺可闻及哮鸣音', '血常规：基本正常');

-- Diagnoses
INSERT INTO `diagnosis` (`diagnosis_id`, `disease_id`, `medical_record_id`, `main_diagnosis_mark`, `suspect_mark`, `onset_date`, `diagnosis_mark`) VALUES
(1, 1, 1, 'Y', 'N', CURDATE() - INTERVAL 3 DAY, 'CONFIRMED'),
(2, 3, 2, 'Y', 'N', CURDATE() - INTERVAL 7 DAY, 'CONFIRMED'),
(3, 8, 3, 'Y', 'N', CURDATE() - INTERVAL 2 DAY, 'CONFIRMED'),
(4, 8, 4, 'N', 'Y', CURDATE() - INTERVAL 1 DAY, 'SUSPECTED'),
(5, 2, 5, 'Y', 'N', CURDATE() - INTERVAL 2 DAY, 'CONFIRMED');

-- Invoices
INSERT INTO `invoice` (`invoice_id`, `invoice_no`, `total_cost`, `is_day_cal`, `pay_time`, `user_id`, `pay_mode_id`) VALUES
(1, 'INV20240101001', 35.00, 'N', NOW(), 10, 21),
(2, 'INV20240101002', 70.00, 'N', NOW(), 10, 22),
(3, 'INV20240101003', 25.00, 'N', NOW(), 10, 23);

-- Expense Items
INSERT INTO `expense_items` (`expense_items_id`, `medical_record_id`, `total_cost`, `pay_status`, `invoice_id`, `expense_type_id`) VALUES
(1, 1, 35.00, 'PAID', 1, 1),
(2, 2, 70.00, 'PAID', 2, 1),
(3, 3, 25.00, 'PAID', 3, 1);

-- Registrations
INSERT INTO `registration` (`registration_id`, `medical_record_id`, `registration_level_id`, `patient_id`, `department_id`, `calculation_type_id`, `doctor_id`, `registration_date`, `buy_medical_record`, `registration_total_cost`, `expense_type_id`, `expense_items_id`, `registration_status`) VALUES
(1, 1, 1, 1, 1, 2, 2, NOW(), 'N', 10.00, 1, 1, 'VISITING'),
(2, 2, 3, 2, 1, 2, 2, NOW() - INTERVAL 1 DAY, 'N', 50.00, 1, 2, 'COMPLETED'),
(3, 3, 2, 3, 1, 1, 3, NOW(), 'N', 20.00, 1, 3, 'VISITING'),
(4, 4, 1, 4, 2, 1, 4, NOW() - INTERVAL 1 DAY, 'N', 10.00, 1, NULL, 'COMPLETED'),
(5, 5, 1, 6, 3, 2, 5, NOW(), 'N', 10.00, 1, NULL, 'VISITING');

-- =====================================================
-- Prescriptions
-- =====================================================

INSERT INTO `prescription` (`prescription_id`, `medical_record_id`, `doctor_id`, `submit_time`, `prescription_type`, `valid_status`) VALUES
(1, 1, 2, NOW(), 'WESTERN', 'VALID'),
(2, 2, 2, NOW() - INTERVAL 1 DAY, 'WESTERN', 'DISPENSED'),
(3, 3, 3, NOW(), 'WESTERN', 'VALID');

INSERT INTO `prescription_items` (`prescription_items_id`, `prescription_id`, `drags_id`, `expense_items_id`, `drugs_usage`, `dosage`, `times`, `days`, `quantity`, `drugs_advice`, `drugs_dispensing_status`, `actual_quantity`) VALUES
(1, 1, 1, NULL, '口服', 0.5, 3, 5, 1, '饭后服用', 'NOT_DISPENSED', 0),
(2, 1, 3, NULL, '口服', 0.3, 2, 3, 1, '发热时服用', 'NOT_DISPENSED', 0),
(3, 2, 11, NULL, '口服', 0.3, 1, 30, 2, '晨起服用', 'DISPENSED', 2),
(4, 2, 4, NULL, '口服', 0.1, 1, 30, 1, '睡前服用', 'DISPENSED', 1),
(5, 3, 1, NULL, '口服', 0.5, 3, 3, 1, '饭后服用', 'NOT_DISPENSED', 0);

-- =====================================================
-- Examinations
-- =====================================================

INSERT INTO `examination` (`examination_id`, `medical_record_id`, `doctor_id`, `examination_mark`, `doctor_advice`, `submit_time`) VALUES
(1, 1, 2, 'LAB', '请空腹抽血', NOW()),
(2, 2, 2, 'LAB', '常规检查', NOW() - INTERVAL 1 DAY),
(3, 3, 3, 'LAB', '常规检查', NOW());

INSERT INTO `examination_fmedical_items` (`examination_fmedical_items_id`, `examination_id`, `fmedical_items_id`, `doctor_id`, `registration_status`, `purpose_requirements`, `quantity`, `actual_quantity`, `examination_result_id`, `expense_items_id`, `valid_status`) VALUES
(1, 1, 1, 2, 'REGISTERED', '血常规检查', 1, 1, NULL, NULL, 'VALID'),
(2, 1, 5, 2, 'REGISTERED', '空腹血糖', 1, 1, NULL, NULL, 'VALID'),
(3, 2, 1, 2, 'COMPLETED', '血常规检查', 1, 1, 1, NULL, 'COMPLETED'),
(4, 2, 3, 2, 'COMPLETED', '肝功能检查', 1, 1, 2, NULL, 'COMPLETED'),
(5, 3, 2, 3, 'REGISTERED', '尿常规检查', 1, 0, NULL, NULL, 'VALID');

INSERT INTO `examination_result` (`examination_result_id`, `doctor_id`, `findings`, `diagnostic_suggestion`, `submit_time`) VALUES
(1, 7, '白细胞计数正常，红细胞计数正常，血小板计数正常', '血常规结果正常', NOW() - INTERVAL 1 DAY),
(2, 7, '肝功能各项指标正常', '肝功能正常', NOW() - INTERVAL 1 DAY);

-- =====================================================
-- Treatments
-- =====================================================

INSERT INTO `treatment` (`treatment_id`, `medical_record_id`, `doctor_id`, `submit_time`) VALUES
(1, 5, 5, NOW());

INSERT INTO `treatment_items` (`treatment_items_id`, `treatment_id`, `fmedical_items_id`, `quantity`, `actual_quantity`, `expense_items_id`, `valid_status`) VALUES
(1, 1, 12, 2, 0, NULL, 'VALID');

-- =====================================================
-- Commonly Used
-- =====================================================

INSERT INTO `commonly_used_diagnosis` (`commonly_used_diagnosis_id`, `doctor_id`, `disease_id`) VALUES
(1, 2, 1),
(2, 2, 3),
(3, 2, 5),
(4, 3, 8),
(5, 3, 5),
(6, 5, 1),
(7, 5, 2);

INSERT INTO `commonly_used_drugs` (`commonly_used_drugs`, `doctor_id`, `drugs_id`) VALUES
(1, 2, 1),
(2, 2, 3),
(3, 2, 5),
(4, 3, 1),
(5, 3, 9);

INSERT INTO `commonly_used_fmedical` (`commonly_used_fmedical_id`, `doctor_id`, `fmedical_items_id`) VALUES
(1, 2, 1),
(2, 2, 3),
(3, 2, 9),
(4, 3, 1),
(5, 3, 2);

-- =====================================================
-- Day Cal
-- =====================================================

INSERT INTO `day_cal` (`day_cal_id`, `user_id`, `day_cal_date`, `yf_total`, `gh_total`, `day_cal_total`, `cl_total`, `jc_total`, `cz_total`, `qt_total`) VALUES
(1, 10, CURDATE() - INTERVAL 1 DAY, 500.00, 200.00, 850.00, 50.00, 80.00, 20.00, 0.00),
(2, 10, CURDATE(), 150.00, 100.00, 250.00, 0.00, 0.00, 0.00, 0.00);

-- =====================================================
-- Group Templates
-- =====================================================

INSERT INTO `group_examination` (`group_examination_id`, `group_examination_name`, `group_examination_code`) VALUES
(1, '入职体检套餐', 'G001'),
(2, '常规体检套餐', 'G002');

INSERT INTO `group_examination_fmedical_items` (`group_examination_fmedical_items_id`, `group_examination_id`, `fmedical_items_id`) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 1, 3),
(4, 1, 4),
(5, 1, 9),
(6, 2, 1),
(7, 2, 2),
(8, 2, 5),
(9, 2, 6),
(10, 2, 9);

INSERT INTO `group_prescription` (`group_prescription_id`, `group_prescription_name`, `group_prescription_code`) VALUES
(1, '感冒处方', 'P001'),
(2, '高血压处方', 'P002');

INSERT INTO `group_prescription_items` (`group_prescription_items_id`, `group_prescription_id`, `drugs_id`) VALUES
(1, 1, 1),
(2, 1, 3),
(3, 1, 7),
(4, 2, 11),
(5, 2, 4);

INSERT INTO `group_treatment` (`group_treatment_id`, `group_treatment_name`, `group_treatment_code`) VALUES
(1, '雾化治疗套餐', 'T001');

INSERT INTO `group_treatment_items` (`group_treatment_items_id`, `group_treatment_id`, `fmedical_items_id`) VALUES
(1, 1, 12);

-- =====================================================
-- Medical Record Home Page Templates
-- =====================================================

INSERT INTO `medical_record_home_page_template` (`medical_record_home_page_template_id`, `doctor_id`, `name`, `scope`, `chief_complaint`, `present_history`, `physical_examination`) VALUES
(1, 2, '上呼吸道感染模板', 'PERSONAL', '发热、咳嗽{天数}天', '患者{天数}天前受凉后出现发热，体温最高{体温}℃，伴咳嗽、咳痰。', '咽部充血，双肺呼吸音粗'),
(2, 2, '高血压复诊模板', 'PERSONAL', '头晕、头痛{天数}天', '患者{天数}天前出现头晕、头痛，血压控制不佳。', '血压偏高，心肺听诊无异常'),
(3, 3, '急性胃肠炎模板', 'PERSONAL', '腹痛、腹泻{天数}天', '患者{天数}天前进食不洁食物后出现腹痛、腹泻。', '腹软，脐周压痛');

INSERT INTO `diagnosis_template` (`diagnosis_template_id`, `medical_record_home_page_template_id`, `disease_id`, `main_diagnosis_mark`, `suspect_mark`) VALUES
(1, 1, 1, 'Y', 'N'),
(2, 2, 3, 'Y', 'N'),
(3, 3, 8, 'Y', 'N');
