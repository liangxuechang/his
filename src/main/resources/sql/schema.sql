-- =====================================================
-- Hospital Information System (HIS) Database Schema
-- Generated based on Java Entity Classes
-- =====================================================

-- Drop all tables if they exist
DROP TABLE IF EXISTS `group_treatment_items`;
DROP TABLE IF EXISTS `group_treatment`;
DROP TABLE IF EXISTS `group_prescription_items`;
DROP TABLE IF EXISTS `group_prescription`;
DROP TABLE IF EXISTS `group_examination_drugs_items`;
DROP TABLE IF EXISTS `group_examination_fmedical_items`;
DROP TABLE IF EXISTS `group_examination`;
DROP TABLE IF EXISTS `examination_result_image`;
DROP TABLE IF EXISTS `examination_result`;
DROP TABLE IF EXISTS `examination_drugs_items`;
DROP TABLE IF EXISTS `examination_fmedical_items`;
DROP TABLE IF EXISTS `examination`;
DROP TABLE IF EXISTS `treatment_items`;
DROP TABLE IF EXISTS `treatment`;
DROP TABLE IF EXISTS `prescription_items`;
DROP TABLE IF EXISTS `prescription`;
DROP TABLE IF EXISTS `diagnosis_template`;
DROP TABLE IF EXISTS `diagnosis`;
DROP TABLE IF EXISTS `medical_record_home_page_template`;
DROP TABLE IF EXISTS `medical_record_home_page`;
DROP TABLE IF EXISTS `medical_record`;
DROP TABLE IF EXISTS `expense_items`;
DROP TABLE IF EXISTS `invoice`;
DROP TABLE IF EXISTS `registration`;
DROP TABLE IF EXISTS `scheduling_info`;
DROP TABLE IF EXISTS `scheduling_rule`;
DROP TABLE IF EXISTS `commonly_used_fmedical`;
DROP TABLE IF EXISTS `commonly_used_drugs`;
DROP TABLE IF EXISTS `commonly_used_diagnosis`;
DROP TABLE IF EXISTS `day_cal`;
DROP TABLE IF EXISTS `workload`;
DROP TABLE IF EXISTS `fmedical_items`;
DROP TABLE IF EXISTS `drugs`;
DROP TABLE IF EXISTS `disease`;
DROP TABLE IF EXISTS `disease_type`;
DROP TABLE IF EXISTS `disease_folder`;
DROP TABLE IF EXISTS `patient`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `department`;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `function`;
DROP TABLE IF EXISTS `registration_level`;
DROP TABLE IF EXISTS `calculation_type`;
DROP TABLE IF EXISTS `expense_type`;
DROP TABLE IF EXISTS `constant_items`;
DROP TABLE IF EXISTS `constant_type`;

-- =====================================================
-- Basic Data Tables
-- =====================================================

-- Constant Type Table (常量类型表)
CREATE TABLE `constant_type` (
    `constant_type_id` INT PRIMARY KEY AUTO_INCREMENT,
    `constant_type_code` VARCHAR(50),
    `constant_type_name` VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常量类型表';

-- Constant Items Table (常量项表)
CREATE TABLE `constant_items` (
    `constant_items_id` INT PRIMARY KEY AUTO_INCREMENT,
    `constant_type_id` INT,
    `constant_items_code` VARCHAR(50),
    `constant_items_name` VARCHAR(100),
    FOREIGN KEY (`constant_type_id`) REFERENCES `constant_type`(`constant_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常量项表';

-- Role Table (角色表)
CREATE TABLE `role` (
    `role_id` INT PRIMARY KEY AUTO_INCREMENT,
    `function_id` INT,
    `role_name` VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- Function Table (功能表)
CREATE TABLE `function` (
    `function_id` INT PRIMARY KEY AUTO_INCREMENT,
    `role_id` INT,
    `function_url` VARCHAR(200),
    `function_name` VARCHAR(100),
    `reverse1` INT,
    FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功能表';

-- Department Table (科室表)
CREATE TABLE `department` (
    `department_id` INT PRIMARY KEY AUTO_INCREMENT,
    `department_code` VARCHAR(50),
    `department_name` VARCHAR(100),
    `department_category_id` INT,
    `department_type` VARCHAR(50),
    FOREIGN KEY (`department_category_id`) REFERENCES `constant_items`(`constant_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

-- User Table (用户表)
CREATE TABLE `user` (
    `user_id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_loginname` VARCHAR(50),
    `user_password` VARCHAR(100),
    `role_id` INT,
    `user_name` VARCHAR(50),
    `department_id` INT,
    `user_title_id` INT,
    `user_gender` VARCHAR(10),
    `user_status` VARCHAR(20),
    `user_scheduling_limitcount` INT DEFAULT 50,
    FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`),
    FOREIGN KEY (`department_id`) REFERENCES `department`(`department_id`),
    FOREIGN KEY (`user_title_id`) REFERENCES `constant_items`(`constant_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- Patient Table (患者表)
CREATE TABLE `patient` (
    `patient_id` INT PRIMARY KEY AUTO_INCREMENT,
    `patient_name` VARCHAR(50),
    `patient_gender` VARCHAR(10),
    `patient_birth` DATE,
    `patient_age` INT,
    `patient_identity` VARCHAR(20),
    `patient_address` VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者表';

-- Registration Level Table (挂号级别表)
CREATE TABLE `registration_level` (
    `registration_level_id` INT PRIMARY KEY AUTO_INCREMENT,
    `registration_level_name` VARCHAR(50),
    `is_default` VARCHAR(10),
    `registration_sequence` INT,
    `registration_cost` DECIMAL(10,2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挂号级别表';

-- Calculation Type Table (结算类型表)
CREATE TABLE `calculation_type` (
    `calculation_type_id` INT PRIMARY KEY AUTO_INCREMENT,
    `calculation_type_name` VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结算类型表';

-- Expense Type Table (费用类型表)
CREATE TABLE `expense_type` (
    `expense_type_id` INT PRIMARY KEY AUTO_INCREMENT,
    `expense_type_code` VARCHAR(50),
    `expense_type_name` VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用类型表';

-- Disease Folder Table (疾病文件夹表)
CREATE TABLE `disease_folder` (
    `disease_folder_id` INT PRIMARY KEY AUTO_INCREMENT,
    `disease_folder_name` VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疾病文件夹表';

-- Disease Type Table (疾病类型表)
CREATE TABLE `disease_type` (
    `disease_type_id` INT PRIMARY KEY AUTO_INCREMENT,
    `disease_type_code` VARCHAR(50),
    `disease_type_name` VARCHAR(100),
    `disease_type_sequence` INT,
    `disease_type_type` VARCHAR(50),
    `disease_folder_id` INT,
    FOREIGN KEY (`disease_folder_id`) REFERENCES `disease_folder`(`disease_folder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疾病类型表';

-- Disease Table (疾病表)
CREATE TABLE `disease` (
    `disease_id` INT PRIMARY KEY AUTO_INCREMENT,
    `disease_code` VARCHAR(50),
    `disease_name` VARCHAR(200),
    `disease_icd` VARCHAR(50),
    `disease_type_id` INT,
    `disease_customize_name1` VARCHAR(100),
    `disease_customize_name2` VARCHAR(100),
    FOREIGN KEY (`disease_type_id`) REFERENCES `disease_type`(`disease_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疾病表';

-- Drugs Table (药品表)
CREATE TABLE `drugs` (
    `drugs_id` INT PRIMARY KEY AUTO_INCREMENT,
    `drugs_code` VARCHAR(50),
    `drugs_name` VARCHAR(100),
    `drugs_format` VARCHAR(50),
    `drugs_unit` VARCHAR(20),
    `drugs_manufacturer` VARCHAR(100),
    `drugs_dosage_id` INT,
    `drugs_type_id` INT,
    `drugs_price` DECIMAL(10,2),
    `drugs_mnemoniccode` VARCHAR(50),
    `create_time` DATETIME,
    `reverse1` VARCHAR(100),
    `reverse2` VARCHAR(100),
    `reverse3` VARCHAR(100),
    FOREIGN KEY (`drugs_dosage_id`) REFERENCES `constant_items`(`constant_items_id`),
    FOREIGN KEY (`drugs_type_id`) REFERENCES `constant_items`(`constant_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品表';

-- Fmedical Items Table (非药品项目表)
CREATE TABLE `fmedical_items` (
    `fmedical_items_id` INT PRIMARY KEY AUTO_INCREMENT,
    `fmedical_items_code` VARCHAR(50),
    `fmedical_items_name` VARCHAR(100),
    `fmedical_items_format` VARCHAR(50),
    `fmedical_items_price` DECIMAL(10,2),
    `expense_type_id` INT,
    `department_id` INT,
    `fmedical_items_mnemoniccode` VARCHAR(50),
    `fmedical_items_type` VARCHAR(50),
    `create_time` DATETIME,
    `reverse1` VARCHAR(100),
    `reverse2` VARCHAR(100),
    `reverse3` VARCHAR(100),
    FOREIGN KEY (`expense_type_id`) REFERENCES `expense_type`(`expense_type_id`),
    FOREIGN KEY (`department_id`) REFERENCES `department`(`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='非药品项目表';

-- =====================================================
-- Medical Record Related Tables
-- =====================================================

-- Medical Record Table (病历表)
CREATE TABLE `medical_record` (
    `medical_record_id` INT PRIMARY KEY AUTO_INCREMENT,
    `doctor_id` INT,
    `is_treament_over` VARCHAR(10),
    `first_diagnosis_doctor_id` INT,
    `final_diagnosis_doctor_id` INT,
    `first_diagnosis_time` DATETIME,
    `final_diagnosis_time` DATETIME,
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`),
    FOREIGN KEY (`first_diagnosis_doctor_id`) REFERENCES `user`(`user_id`),
    FOREIGN KEY (`final_diagnosis_doctor_id`) REFERENCES `user`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病历表';

-- Medical Record Home Page Table (病历首页表)
CREATE TABLE `medical_record_home_page` (
    `medical_record_home_page_id` INT PRIMARY KEY AUTO_INCREMENT,
    `medical_record_id` INT,
    `doctor_id` INT,
    `chief_complaint` TEXT,
    `present_history` TEXT,
    `present_treatment` TEXT,
    `past_history` TEXT,
    `allergic_history` TEXT,
    `physical_examination` TEXT,
    `assistant_examination` TEXT,
    FOREIGN KEY (`medical_record_id`) REFERENCES `medical_record`(`medical_record_id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病历首页表';

-- Diagnosis Table (诊断表)
CREATE TABLE `diagnosis` (
    `diagnosis_id` INT PRIMARY KEY AUTO_INCREMENT,
    `disease_id` INT,
    `medical_record_id` INT,
    `main_diagnosis_mark` VARCHAR(10),
    `suspect_mark` VARCHAR(10),
    `onset_date` DATE,
    `diagnosis_mark` VARCHAR(10),
    FOREIGN KEY (`disease_id`) REFERENCES `disease`(`disease_id`),
    FOREIGN KEY (`medical_record_id`) REFERENCES `medical_record`(`medical_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊断表';

-- Medical Record Home Page Template Table (病历首页模板表)
CREATE TABLE `medical_record_home_page_template` (
    `medical_record_home_page_template_id` INT PRIMARY KEY AUTO_INCREMENT,
    `doctor_id` INT,
    `name` VARCHAR(100),
    `scope` VARCHAR(50),
    `chief_complaint` TEXT,
    `present_history` TEXT,
    `physical_examination` TEXT,
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病历首页模板表';

-- Diagnosis Template Table (诊断模板表)
CREATE TABLE `diagnosis_template` (
    `diagnosis_template_id` INT PRIMARY KEY AUTO_INCREMENT,
    `medical_record_home_page_template_id` INT,
    `disease_id` INT,
    `main_diagnosis_mark` VARCHAR(10),
    `suspect_mark` VARCHAR(10),
    FOREIGN KEY (`medical_record_home_page_template_id`) REFERENCES `medical_record_home_page_template`(`medical_record_home_page_template_id`),
    FOREIGN KEY (`disease_id`) REFERENCES `disease`(`disease_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊断模板表';

-- =====================================================
-- Registration Related Tables
-- =====================================================

-- Invoice Table (发票表)
CREATE TABLE `invoice` (
    `invoice_id` INT PRIMARY KEY AUTO_INCREMENT,
    `invoice_no` VARCHAR(50),
    `total_cost` DECIMAL(10,2),
    `is_day_cal` VARCHAR(10),
    `pay_time` DATETIME,
    `user_id` INT,
    `pay_mode_id` INT,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`),
    FOREIGN KEY (`pay_mode_id`) REFERENCES `constant_items`(`constant_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票表';

-- Expense Items Table (费用项目表)
CREATE TABLE `expense_items` (
    `expense_items_id` INT PRIMARY KEY AUTO_INCREMENT,
    `medical_record_id` INT,
    `total_cost` DECIMAL(10,2),
    `pay_status` VARCHAR(20),
    `invoice_id` INT,
    `expense_type_id` INT,
    FOREIGN KEY (`medical_record_id`) REFERENCES `medical_record`(`medical_record_id`),
    FOREIGN KEY (`invoice_id`) REFERENCES `invoice`(`invoice_id`),
    FOREIGN KEY (`expense_type_id`) REFERENCES `expense_type`(`expense_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用项目表';

-- Registration Table (挂号表)
CREATE TABLE `registration` (
    `registration_id` INT PRIMARY KEY AUTO_INCREMENT,
    `medical_record_id` INT,
    `registration_level_id` INT,
    `patient_id` INT,
    `department_id` INT,
    `calculation_type_id` INT,
    `doctor_id` INT,
    `registration_date` DATETIME,
    `buy_medical_record` VARCHAR(10),
    `registration_total_cost` DECIMAL(10,2),
    `expense_type_id` INT,
    `expense_items_id` INT,
    `registration_status` VARCHAR(20),
    FOREIGN KEY (`medical_record_id`) REFERENCES `medical_record`(`medical_record_id`),
    FOREIGN KEY (`registration_level_id`) REFERENCES `registration_level`(`registration_level_id`),
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`patient_id`),
    FOREIGN KEY (`department_id`) REFERENCES `department`(`department_id`),
    FOREIGN KEY (`calculation_type_id`) REFERENCES `calculation_type`(`calculation_type_id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`),
    FOREIGN KEY (`expense_type_id`) REFERENCES `expense_type`(`expense_type_id`),
    FOREIGN KEY (`expense_items_id`) REFERENCES `expense_items`(`expense_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挂号表';

-- Scheduling Rule Table (排班规则表)
CREATE TABLE `scheduling_rule` (
    `scheduling_rule_id` INT PRIMARY KEY AUTO_INCREMENT,
    `doctor_id` INT,
    `scheduling_rule_noonbreak` VARCHAR(20),
    `scheduling_rule_starttime` TIME,
    `scheduling_rule_endtime` TIME,
    `scheduling_rule_weekday` VARCHAR(20),
    `scheduling_rule_limitcount` INT,
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班规则表';

-- Scheduling Info Table (排班信息表)
CREATE TABLE `scheduling_info` (
    `scheduling_info_id` INT PRIMARY KEY AUTO_INCREMENT,
    `doctor_id` INT,
    `scheduling_noonbreak` VARCHAR(20),
    `scheduling_starttime` DATETIME,
    `scheduling_endtime` DATETIME,
    `scheduling_weekday` VARCHAR(20),
    `scheduling_limitcount` INT,
    `scheduling_restcount` INT,
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班信息表';

-- =====================================================
-- Prescription Related Tables
-- =====================================================

-- Prescription Table (处方表)
CREATE TABLE `prescription` (
    `prescription_id` INT PRIMARY KEY AUTO_INCREMENT,
    `medical_record_id` INT,
    `doctor_id` INT,
    `submit_time` DATETIME,
    `prescription_type` VARCHAR(20),
    `valid_status` VARCHAR(20),
    FOREIGN KEY (`medical_record_id`) REFERENCES `medical_record`(`medical_record_id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方表';

-- Prescription Items Table (处方项目表)
CREATE TABLE `prescription_items` (
    `prescription_items_id` INT PRIMARY KEY AUTO_INCREMENT,
    `prescription_id` INT,
    `drags_id` INT,
    `expense_items_id` INT,
    `drugs_usage` VARCHAR(100),
    `dosage` DECIMAL(10,2),
    `times` INT,
    `days` INT,
    `quantity` INT,
    `drugs_advice` VARCHAR(200),
    `drugs_dispensing_status` VARCHAR(20),
    `actual_quantity` INT,
    FOREIGN KEY (`prescription_id`) REFERENCES `prescription`(`prescription_id`),
    FOREIGN KEY (`drags_id`) REFERENCES `drugs`(`drugs_id`),
    FOREIGN KEY (`expense_items_id`) REFERENCES `expense_items`(`expense_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方项目表';

-- =====================================================
-- Examination Related Tables
-- =====================================================

-- Examination Table (检查检验表)
CREATE TABLE `examination` (
    `examination_id` INT PRIMARY KEY AUTO_INCREMENT,
    `medical_record_id` INT,
    `doctor_id` INT,
    `examination_mark` VARCHAR(20),
    `doctor_advice` VARCHAR(500),
    `submit_time` DATETIME,
    FOREIGN KEY (`medical_record_id`) REFERENCES `medical_record`(`medical_record_id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检查检验表';

-- Examination Fmedical Items Table (检查检验非药品项目表)
CREATE TABLE `examination_fmedical_items` (
    `examination_fmedical_items_id` INT PRIMARY KEY AUTO_INCREMENT,
    `examination_id` INT,
    `fmedical_items_id` INT,
    `doctor_id` INT,
    `registration_status` VARCHAR(20),
    `purpose_requirements` VARCHAR(500),
    `quantity` INT,
    `actual_quantity` INT,
    `examination_result_id` INT,
    `expense_items_id` INT,
    `valid_status` VARCHAR(20),
    FOREIGN KEY (`examination_id`) REFERENCES `examination`(`examination_id`),
    FOREIGN KEY (`fmedical_items_id`) REFERENCES `fmedical_items`(`fmedical_items_id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`),
    FOREIGN KEY (`expense_items_id`) REFERENCES `expense_items`(`expense_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检查检验非药品项目表';

-- Examination Drugs Items Table (检查检验药品项目表)
CREATE TABLE `examination_drugs_items` (
    `examination_drugs_items_id` INT PRIMARY KEY AUTO_INCREMENT,
    `examination_fmedical_items_id` INT,
    `drugs_id` INT,
    `doctor_id` INT,
    `drugs_usage` VARCHAR(100),
    `quantity` INT,
    `actual_quantity` INT,
    `drugs_dispensing_status` VARCHAR(20),
    `expense_items_id` INT,
    `times` INT,
    `days` INT,
    `dosage` DECIMAL(10,2),
    FOREIGN KEY (`examination_fmedical_items_id`) REFERENCES `examination_fmedical_items`(`examination_fmedical_items_id`),
    FOREIGN KEY (`drugs_id`) REFERENCES `drugs`(`drugs_id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`),
    FOREIGN KEY (`expense_items_id`) REFERENCES `expense_items`(`expense_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检查检验药品项目表';

-- Examination Result Table (检查检验结果表)
CREATE TABLE `examination_result` (
    `examination_result_id` INT PRIMARY KEY AUTO_INCREMENT,
    `doctor_id` INT,
    `findings` TEXT,
    `diagnostic_suggestion` TEXT,
    `submit_time` DATETIME,
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检查检验结果表';

-- Examination Result Image Table (检查检验结果图片表)
CREATE TABLE `examination_result_image` (
    `examination_result_image_id` INT PRIMARY KEY AUTO_INCREMENT,
    `examination_result_id` INT,
    `image_url` VARCHAR(200),
    `image_name` VARCHAR(100),
    FOREIGN KEY (`examination_result_id`) REFERENCES `examination_result`(`examination_result_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检查检验结果图片表';

-- =====================================================
-- Treatment Related Tables
-- =====================================================

-- Treatment Table (处置表)
CREATE TABLE `treatment` (
    `treatment_id` INT PRIMARY KEY AUTO_INCREMENT,
    `medical_record_id` INT,
    `doctor_id` INT,
    `submit_time` DATETIME,
    FOREIGN KEY (`medical_record_id`) REFERENCES `medical_record`(`medical_record_id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处置表';

-- Treatment Items Table (处置项目表)
CREATE TABLE `treatment_items` (
    `treatment_items_id` INT PRIMARY KEY AUTO_INCREMENT,
    `treatment_id` INT,
    `fmedical_items_id` INT,
    `quantity` INT,
    `actual_quantity` INT,
    `expense_items_id` INT,
    `valid_status` VARCHAR(20),
    FOREIGN KEY (`treatment_id`) REFERENCES `treatment`(`treatment_id`),
    FOREIGN KEY (`fmedical_items_id`) REFERENCES `fmedical_items`(`fmedical_items_id`),
    FOREIGN KEY (`expense_items_id`) REFERENCES `expense_items`(`expense_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处置项目表';

-- =====================================================
-- Group Related Tables (for batch operations)
-- =====================================================

-- Group Examination Table (成组检查检验表)
CREATE TABLE `group_examination` (
    `group_examination_id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_examination_name` VARCHAR(100),
    `group_examination_code` VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成组检查检验表';

-- Group Examination Fmedical Items Table (成组检查检验非药品项目表)
CREATE TABLE `group_examination_fmedical_items` (
    `group_examination_fmedical_items_id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_examination_id` INT,
    `fmedical_items_id` INT,
    FOREIGN KEY (`group_examination_id`) REFERENCES `group_examination`(`group_examination_id`),
    FOREIGN KEY (`fmedical_items_id`) REFERENCES `fmedical_items`(`fmedical_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成组检查检验非药品项目表';

-- Group Examination Drugs Items Table (成组检查检验药品项目表)
CREATE TABLE `group_examination_drugs_items` (
    `group_examination_drugs_items_id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_examination_id` INT,
    `drugs_id` INT,
    FOREIGN KEY (`group_examination_id`) REFERENCES `group_examination`(`group_examination_id`),
    FOREIGN KEY (`drugs_id`) REFERENCES `drugs`(`drugs_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成组检查检验药品项目表';

-- Group Prescription Table (成组处方表)
CREATE TABLE `group_prescription` (
    `group_prescription_id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_prescription_name` VARCHAR(100),
    `group_prescription_code` VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成组处方表';

-- Group Prescription Items Table (成组处方项目表)
CREATE TABLE `group_prescription_items` (
    `group_prescription_items_id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_prescription_id` INT,
    `drugs_id` INT,
    FOREIGN KEY (`group_prescription_id`) REFERENCES `group_prescription`(`group_prescription_id`),
    FOREIGN KEY (`drugs_id`) REFERENCES `drugs`(`drugs_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成组处方项目表';

-- Group Treatment Table (成组处置表)
CREATE TABLE `group_treatment` (
    `group_treatment_id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_treatment_name` VARCHAR(100),
    `group_treatment_code` VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成组处置表';

-- Group Treatment Items Table (成组处置项目表)
CREATE TABLE `group_treatment_items` (
    `group_treatment_items_id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_treatment_id` INT,
    `fmedical_items_id` INT,
    FOREIGN KEY (`group_treatment_id`) REFERENCES `group_treatment`(`group_treatment_id`),
    FOREIGN KEY (`fmedical_items_id`) REFERENCES `fmedical_items`(`fmedical_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成组处置项目表';

-- =====================================================
-- Commonly Used Tables
-- =====================================================

-- Commonly Used Diagnosis Table (常用诊断表)
CREATE TABLE `commonly_used_diagnosis` (
    `commonly_used_diagnosis_id` INT PRIMARY KEY AUTO_INCREMENT,
    `doctor_id` INT,
    `disease_id` INT,
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`),
    FOREIGN KEY (`disease_id`) REFERENCES `disease`(`disease_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常用诊断表';

-- Commonly Used Drugs Table (常用药品表)
CREATE TABLE `commonly_used_drugs` (
    `commonly_used_drugs` INT PRIMARY KEY AUTO_INCREMENT,
    `doctor_id` INT,
    `drugs_id` INT,
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`),
    FOREIGN KEY (`drugs_id`) REFERENCES `drugs`(`drugs_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常用药品表';

-- Commonly Used Fmedical Table (常用非药品表)
CREATE TABLE `commonly_used_fmedical` (
    `commonly_used_fmedical_id` INT PRIMARY KEY AUTO_INCREMENT,
    `doctor_id` INT,
    `fmedical_items_id` INT,
    FOREIGN KEY (`doctor_id`) REFERENCES `user`(`user_id`),
    FOREIGN KEY (`fmedical_items_id`) REFERENCES `fmedical_items`(`fmedical_items_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常用非药品表';

-- =====================================================
-- Statistics Related Tables
-- =====================================================

-- Day Cal Table (日结表)
CREATE TABLE `day_cal` (
    `day_cal_id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT,
    `day_cal_date` DATE,
    `yf_total` DECIMAL(10,2),
    `gh_total` DECIMAL(10,2),
    `day_cal_total` DECIMAL(10,2),
    `cl_total` DECIMAL(10,2),
    `jc_total` DECIMAL(10,2),
    `cz_total` DECIMAL(10,2),
    `qt_total` DECIMAL(10,2),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日结表';

-- Workload Table (工作量统计表)
CREATE TABLE `workload` (
    `workload_id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT,
    `department_id` INT,
    `workload_date` DATE,
    `registration_count` INT DEFAULT 0,
    `prescription_count` INT DEFAULT 0,
    `examination_count` INT DEFAULT 0,
    `treatment_count` INT DEFAULT 0,
    `total_workload` DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`),
    FOREIGN KEY (`department_id`) REFERENCES `department`(`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作量统计表';

-- =====================================================
-- Indexes
-- =====================================================

CREATE INDEX idx_user_loginname ON `user`(`user_loginname`);
CREATE INDEX idx_user_department ON `user`(`department_id`);
CREATE INDEX idx_patient_identity ON `patient`(`patient_identity`);
CREATE INDEX idx_registration_patient ON `registration`(`patient_id`);
CREATE INDEX idx_registration_department ON `registration`(`department_id`);
CREATE INDEX idx_registration_doctor ON `registration`(`doctor_id`);
CREATE INDEX idx_registration_date ON `registration`(`registration_date`);
CREATE INDEX idx_medical_record_doctor ON `medical_record`(`doctor_id`);
CREATE INDEX idx_drugs_code ON `drugs`(`drugs_code`);
CREATE INDEX idx_drugs_name ON `drugs`(`drugs_name`);
CREATE INDEX idx_fmedical_items_code ON `fmedical_items`(`fmedical_items_code`);
CREATE INDEX idx_disease_icd ON `disease`(`disease_icd`);
CREATE INDEX idx_scheduling_info_doctor ON `scheduling_info`(`doctor_id`);
CREATE INDEX idx_scheduling_rule_doctor ON `scheduling_rule`(`doctor_id`);
