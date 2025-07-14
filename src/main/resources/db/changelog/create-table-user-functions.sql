CREATE TABLE user_functions (
    id UUID PRIMARY KEY,
    function VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_user_function_user FOREIGN KEY (user_id) REFERENCES users(id)
);