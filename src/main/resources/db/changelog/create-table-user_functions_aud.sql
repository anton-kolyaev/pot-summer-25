CREATE TABLE IF NOT EXISTS user_functions_aud (
    id UUID,
    rev INTEGER,
    revtype SMALLINT,
    function VARCHAR(50),
    user_id UUID,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_user_functions_aud_rev FOREIGN KEY (rev) REFERENCES revinfo (id)
);