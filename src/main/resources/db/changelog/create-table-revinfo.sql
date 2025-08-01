CREATE TABLE revinfo (
  id INT PRIMARY KEY,
  timestamp BIGINT NOT NULL,
  created_by UUID,
  revision_reason TEXT
);