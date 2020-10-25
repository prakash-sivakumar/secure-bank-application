-- noinspection SqlDialectInspectionForFile

DROP DATABASE bank;
CREATE DATABASE bank;

DROP TABLE IF EXISTS bank.user;
CREATE TABLE bank.user (
  user_id int(11) unsigned NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  gender varchar(2) NOT NULL,
  dob date NOT NULL,
  contact varchar(12) NOT NULL unique ,
  email_id varchar(255) NOT NULL unique ,
  address varchar(255) NOT NULL,
  user_type int(2) NOT NULL,
  created timestamp DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE bank.user AUTO_INCREMENT=1000;

DROP TABLE IF EXISTS bank.employee;
CREATE TABLE bank.employee
(
    employee_id int(11) unsigned NOT NULL AUTO_INCREMENT,
    employee_name varchar(255) NOT NULL,
    gender varchar(255) NOT NULL,
    age int(11) NOT NULL,
    tier_level int(11) unsigned NOT NULL,
    designation_id int(11) NOT NULL,
    contact_no varchar(255) NOT NULL unique ,
    email_id varchar(255) NOT NULL unique ,
    address varchar(255) NOT NULL,
    created timestamp DEFAULT CURRENT_TIMESTAMP(),
    updated timestamp DEFAULT CURRENT_TIMESTAMP(),
    PRIMARY KEY (employee_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE bank.employee AUTO_INCREMENT=1000;

INSERT INTO bank.employee (employee_id, employee_name,gender,age, tier_level, designation_id,contact_no,email_id,address)
VALUES (1000,"Tier 1","M",23,2,1,"4523456789","user.tier1.sb@gmail.com","Tempe,AZ");
INSERT INTO bank.employee (employee_id, employee_name,gender,age, tier_level, designation_id,contact_no,email_id,address)
VALUES (1001,"Tier 2","M",25,3,2,"4083456789","user.tier2.sb@gmail.com","Tempe,AZ");
INSERT INTO bank.employee (employee_id, employee_name,gender,age, tier_level, designation_id,contact_no,email_id,address)
VALUES (1002,"Admin","M",24,1,1,"4023456789","user.tier3.sb@gmail.com","Tempe,AZ");

DROP TABLE IF EXISTS bank.account;
CREATE TABLE bank.account (
  account_no int(11) unsigned NOT NULL AUTO_INCREMENT,
  user_id int(11) unsigned NOT NULL,
  balance decimal(10,2) NOT NULL,
  routing_no int(11) NOT NULL,
  account_type int(2) NOT NULL,
  interest decimal(5,2),
  created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (account_no),
  FOREIGN KEY (user_id) REFERENCES bank.user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE bank.account AUTO_INCREMENT=1000000;

DROP TABLE IF EXISTS  bank.transaction_request;
CREATE TABLE bank.transaction_request (
  request_id int(11) NOT NULL AUTO_INCREMENT,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP(),
  status_id int(11) NOT NULL,
  created_by varchar(255) NOT NULL,
  approved_by varchar(255),
  approved_at timestamp null,
  from_account int(11) unsigned NOT NULL,
  to_account int(11) unsigned,
  description varchar(255),
  type int(2) NOT NULL,
  transaction_amount decimal(10,2) NOT NULL,
  critical int(2) NOT NULL DEFAULT 0,
  PRIMARY KEY (request_id),
  FOREIGN KEY (from_account) REFERENCES bank.account(account_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS bank.transaction;
CREATE TABLE bank.transaction (
  transaction_id int(11) unsigned NOT NULL AUTO_INCREMENT,
  transaction_amount decimal(10,2) NOT NULL,
  transaction_timestamp timestamp DEFAULT CURRENT_TIMESTAMP(),
  transaction_type int(1) NOT NULL,
  description varchar(255),
  status int(1),
  account_no int(11) unsigned NOT NULL,
  balance decimal(10,2),
  request_id int(11),
  PRIMARY KEY (transaction_id),
  FOREIGN KEY (account_no) REFERENCES bank.account(account_no),
  FOREIGN KEY (request_id) REFERENCES bank.transaction_request(request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO bank.user (name, gender,dob, contact,email_id, address,user_type) values('User 1', 'M', CURRENT_TIMESTAMP(), '4805775641', 'user.user1.sb@gmail.com', '2430 S MILL AVE, TEMPE',1 );
INSERT INTO bank.account(user_id,balance ,routing_no,account_type, interest) VALUES (1000, 5000.0, 45612, 1, 5.0);
INSERT INTO bank.account(user_id,balance ,routing_no,account_type, interest) VALUES (1000, 2500.0, 45622, 2, 12.0);


-- INSERT INTO bank.transaction_request (request_id, status_id,created_by,approved_by,from_account,to_account,transaction_amount) VALUES(1,0,1000,1,1000,1001,200.0);

-- INSERT INTO bank.transaction (transaction_amount,transaction_type,description,status,account_no,request_id)values(200.0,1,'Just transfering',0,10000000000,1);
-- INSERT INTO bank.transaction (transaction_amount,transaction_type,description,status,account_no,request_id)values(200.0,2,'Just transfering',0,1000000000,1);


DROP TABLE IF EXISTS bank.auth_user_role;
DROP TABLE IF EXISTS bank.auth_role;
DROP TABLE IF EXISTS bank.auth_user;
CREATE TABLE bank.auth_role (
  auth_role_id int(11) NOT NULL AUTO_INCREMENT,
  role_name varchar(255) DEFAULT NULL,
  role_desc varchar(255) DEFAULT NULL,
  PRIMARY KEY (auth_role_id)
);
INSERT INTO bank.auth_role VALUES (1,'ADMIN','Administrator');
INSERT INTO bank.auth_role VALUES (2,'TIER1','Tier 1 Employee');
INSERT INTO bank.auth_role VALUES (3,'TIER2','Tier 2 Employee');
INSERT INTO bank.auth_role VALUES (4,'USER','Bank User');
INSERT INTO bank.auth_role VALUES (5,'MERCHANT','Merchant customers');


CREATE TABLE bank.auth_user (
  auth_user_id int(11) NOT NULL AUTO_INCREMENT,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  email varchar(255) NOT NULL unique ,
  password varchar(255) NOT NULL,
  status varchar(255),
  otp int(11),
  expiry timestamp,
  PRIMARY KEY (auth_user_id)
);

CREATE TABLE bank.auth_user_role (
  auth_user_id int(11) NOT NULL,
  auth_role_id int(11) NOT NULL,
  PRIMARY KEY (auth_user_id,auth_role_id),
  KEY FK_user_role (auth_role_id),
  CONSTRAINT FK_auth_user FOREIGN KEY (auth_user_id) REFERENCES auth_user (auth_user_id),
  CONSTRAINT FK_auth_user_role FOREIGN KEY (auth_role_id) REFERENCES auth_role (auth_role_id)
) ;

insert into bank.auth_user (auth_user_id,first_name,last_name,email,password,status) values (1,'Admin','admin','user.tier3.sb@gmail.com','$2a$10$98BWDboGjCPKoYwh9T4xLO5wx1kjkg8k3k76FOWnXu610mWIE3Qhm','VERIFIED');
insert into bank.auth_user_role (auth_user_id, auth_role_id) values ('1','1');


DROP TABLE IF EXISTS bank.admin_log;
CREATE TABLE bank.admin_log (
  id int(11) unsigned NOT NULL AUTO_INCREMENT,
  log_timestamp timestamp ,
  related_user_id varchar(255) NOT NULL,
  message varchar(255),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- INSERT INTO bank.admin_log (id,log_timestamp,related_user_id,message)
-- VALUES (1,'2019-03-02 00:00:00',3,"Transaction Success");
-- INSERT INTO bank.admin_log (id,log_timestamp,related_user_id,message)
-- VALUES (2,'2019-03-02 00:00:00',4,"Transaction Failure");
-- INSERT INTO bank.admin_log (id,log_timestamp,related_user_id,message)
-- VALUES (3,'2019-03-02 00:00:00',5,"Transaction Success");

DROP TABLE IF EXISTS bank.cards;
CREATE TABLE bank.cards (
  card_id int(11) unsigned NOT NULL AUTO_INCREMENT,
  account_no int(11) unsigned NOT NULL,
  balance decimal(10,2) NOT NULL,
  credit_limit decimal(10,2) NOT NULL,
  type int(2) NOT NULL,
  created timestamp DEFAULT CURRENT_TIMESTAMP(),
  updated timestamp DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (card_id),
  FOREIGN KEY (account_no) REFERENCES bank.account(account_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE bank.cards AUTO_INCREMENT=8000000;

-- INSERT INTO bank.cards(account_no ,balance,credit_limit, type) VALUES (1000000, 2500.0, 45622, 2);
-- INSERT INTO bank.cards(account_no ,balance,limit, type) VALUES (10000000000, 2500.0, 45622, 2);

DROP TABLE IF EXISTS bank.checks;
CREATE TABLE bank.checks (
  check_id int(11) unsigned NOT NULL AUTO_INCREMENT,
  account_no int(11) unsigned NOT NULL,
  amount decimal(10,2) NOT NULL,
  issued_at timestamp DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (check_id),
  FOREIGN KEY (account_no) REFERENCES bank.account(account_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `bank`.`checks`(`account_no`,`amount`)VALUES(1000000,500);
INSERT INTO `bank`.`checks`(`account_no`,`amount`)VALUES(1000001,700);


insert into bank.auth_user (auth_user_id,first_name,last_name,email,password,status) values (2,'Tier 1','Tier 1','user.tier1.sb@gmail.com','$2a$10$98BWDboGjCPKoYwh9T4xLO5wx1kjkg8k3k76FOWnXu610mWIE3Qhm','VERIFIED');
insert into bank.auth_user_role (auth_user_id, auth_role_id) values ('2','2');

insert into bank.auth_user (auth_user_id,first_name,last_name,email,password,status) values (3,'Tier 2','Tier 2','user.tier2.sb@gmail.com','$2a$10$98BWDboGjCPKoYwh9T4xLO5wx1kjkg8k3k76FOWnXu610mWIE3Qhm','VERIFIED');
insert into bank.auth_user_role (auth_user_id, auth_role_id) values ('3','3');

insert into bank.auth_user (auth_user_id,first_name,last_name,email,password,status) values (4,'User','1','user.user1.sb@gmail.com','$2a$10$98BWDboGjCPKoYwh9T4xLO5wx1kjkg8k3k76FOWnXu610mWIE3Qhm','VERIFIED');
insert into bank.auth_user_role (auth_user_id, auth_role_id) values ('4','4');


DROP TABLE IF EXISTS  bank.account_request;
CREATE TABLE bank.account_request (
  request_id int(11) NOT NULL AUTO_INCREMENT,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP(),
  status_id int(11) NOT NULL,
  created_by varchar(255) NOT NULL,
  approved_by varchar(255),
  approved_at timestamp null,
  description varchar(255),
  type int(2) NOT NULL,
  account VARCHAR(1024),
  user VARCHAR(1024),
  employee VARCHAR(1024),
  role int(2) NOT NULL,
  PRIMARY KEY (request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS bank.help_page;
CREATE TABLE bank.help_page (
  help_id int(11) unsigned NOT NULL AUTO_INCREMENT,
  auth_user_id int(11),
  mobile varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  title varchar(255) NOT NULL,
  shortdescription varchar(255) NOT NULL,
  PRIMARY KEY (help_id)
);


DROP TRIGGER IF EXISTS  bank.account_trigger;
delimiter //
CREATE TRIGGER bank.account_trigger BEFORE DELETE ON bank.account
FOR EACH ROW
BEGIN
DELETE FROM bank.transaction_request where from_account = OLD.account_no;
DELETE FROM bank.transaction_request where request_id in
(SELECT request_id from bank.transaction where account_no = OLD.account_no);
DELETE FROM bank.transaction where account_no = OLD.account_no;
DELETE FROM bank.cards where account_no = OLD.account_no;
DELETE FROM bank.checks where account_no = OLD.account_no;
END;//
delimiter ;


-- INSERT INTO bank.help_page(help_id, auth_user_id, mobile, email, title, shortdescription) values(1,1,'480-452-4823','abc@gmail.com','Error Page','Error in help page');

