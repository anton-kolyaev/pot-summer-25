--liquibase formatted sql

--changeset Edgar:uuid-ossp-extension

--preconditions onFail:MARK_RAN
--  not sqlCheck expectedResult=0 SELECT COUNT(*) FROM pg_extension WHERE extname = 'uuid-ossp'
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";