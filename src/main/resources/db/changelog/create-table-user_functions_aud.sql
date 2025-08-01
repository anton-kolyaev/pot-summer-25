CREATE TABLE IF NOT EXISTS user_functions_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    function VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_user_functions_aud_rev FOREIGN KEY (rev) REFERENCES revinfo (id)
);