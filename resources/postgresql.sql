CREATE TABLE exceptions
(
  id BIGSERIAL NOT NULL,
  guid UUID NOT NULL,
  application_name VARCHAR(50) NOT NULL,
  machine_name VARCHAR(50) NOT NULL,
  creation_date timestamp NOT NULL,
  type VARCHAR(100) NOT NULL,
  is_protected BOOLEAN NOT NULL DEFAULT false,
  host VARCHAR(100) NULL,
  url VARCHAR(500) NULL,
  http_method VARCHAR(10) NULL,
  ip_address VARCHAR(40) NULL,
  source VARCHAR(100) NULL,
  message VARCHAR(1000) NULL,
  detail TEXT NULL,
  status_code INT NULL,
  sql TEXT NULL,
  deletion_date TIMESTAMP NULL,
  full_json TEXT NULL,
  error_hash INT NULL,
  duplicate_count int NOT NULL DEFAULT 1,

  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX ix_exceptions_guid_applicationname_deletiondate_creationdate ON exceptions
(
  guid ASC,
  application_name ASC,
  deletion_date ASC,
  creation_date DESC
);

CREATE INDEX ix_exceptions_errorhash_applicationname_creationdate_deletiondate ON exceptions
(
  error_hash ASC,
  application_name ASC,
  creation_date DESC,
  deletion_date ASC
);

CREATE INDEX ix_exceptions_applicationname_deletiondate_creation_filtered ON exceptions
(
  application_name ASC,
  deletion_date ASC,
  creation_date DESC
)
WHERE deletion_date IS NULL;
