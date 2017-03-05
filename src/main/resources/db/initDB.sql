CREATE TABLE IF NOT EXISTS companies (
  id      INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name    VARCHAR(100) NOT NULL,
  address VARCHAR(100) NOT NULL,
  country VARCHAR(100) NOT NULL,
  city    VARCHAR(100) NOT NULL,

  INDEX (name)
)
  ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS developers (
  id         INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(100) NOT NULL,
  age        INT          NOT NULL,
  country    VARCHAR(100) NOT NULL,
  city       VARCHAR(100) NOT NULL,
  join_date  DATE,

  INDEX (name)
)
  ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS skills (
  skill_id          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  skill_description VARCHAR(100) NOT NULL,

  INDEX (skill_description)
)
  ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS dev_skills (
  developer_id INT NOT NULL,
  skills_id    INT NOT NULL,

  FOREIGN KEY (developer_id) REFERENCES developers (id),
  FOREIGN KEY (skills_id) REFERENCES skills (skill_id),
  UNIQUE (developer_id, skills_id)
)
  ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS projects (
  id          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100) NOT NULL,
  description VARCHAR(250) NOT NULL
)
  ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS dev_projects (
  developer_id INT NOT NULL,
  project_id   INT NOT NULL,

  FOREIGN KEY (developer_id) REFERENCES developers (id),
  FOREIGN KEY (project_id) REFERENCES projects (id),
  UNIQUE (developer_id, project_id)
)
  ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS project_company (
  project_id INT NOT NULL,
  company_id INT NOT NULL,

  FOREIGN KEY (project_id) REFERENCES projects (id),
  FOREIGN KEY (company_id) REFERENCES projects (id),
  UNIQUE (project_id, company_id)
)
  ENGINE InnoDB;


CREATE TABLE IF NOT EXISTS customers (
  id     INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name   VARCHAR(100) NOT NULL,
  inn    INT,
  edrpou INT,

  INDEX name (name),
  INDEX inn (inn),
  INDEX edrpou (edrpou)
)
  ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS projects_customers (
  project_id   INT NOT NULL,
  customers_id INT NOT NULL,
  FOREIGN KEY (customers_id) REFERENCES customers (id),
  FOREIGN KEY (project_id) REFERENCES projects (id
  ),
  UNIQUE (customers_id, project_id)
)
  ENGINE InnoDB;