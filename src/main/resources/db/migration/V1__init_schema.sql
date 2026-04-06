CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    description TEXT,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    due_date DATE,
    priority VARCHAR(100) NOT NULL,
    tags TEXT
);

CREATE TABLE task_attachments (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    file_name VARCHAR(300) NOT NULL,
    stored_file_name VARCHAR(300) NOT NULL,
    content_type VARCHAR(200),
    size_bytes BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL
);
