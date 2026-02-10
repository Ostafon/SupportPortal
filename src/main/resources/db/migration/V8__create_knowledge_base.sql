-- V8: Alter Knowledge Base tables to add new fields

-- Categories
ALTER TABLE knowledge_categories
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS display_order INTEGER DEFAULT 999;

-- Articles
ALTER TABLE knowledge_articles
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'DRAFT',
    ADD COLUMN IF NOT EXISTS view_count BIGINT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS helpful_count BIGINT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS not_helpful_count BIGINT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS is_featured BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS published_at TIMESTAMP;

-- Backfill status based on legacy is_published flag
UPDATE knowledge_articles
SET status = CASE WHEN is_published = true THEN 'PUBLISHED' ELSE 'DRAFT' END
WHERE status IS NULL;

-- Tags table
CREATE TABLE IF NOT EXISTS article_tags (
    article_id BIGINT NOT NULL REFERENCES knowledge_articles(id) ON DELETE CASCADE,
    tag VARCHAR(50) NOT NULL,
    PRIMARY KEY (article_id, tag)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_knowledge_articles_status ON knowledge_articles(status);
CREATE INDEX IF NOT EXISTS idx_knowledge_articles_category ON knowledge_articles(category_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_articles_author ON knowledge_articles(author_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_articles_featured ON knowledge_articles(is_featured);
CREATE INDEX IF NOT EXISTS idx_article_tags_tag ON article_tags(tag);

