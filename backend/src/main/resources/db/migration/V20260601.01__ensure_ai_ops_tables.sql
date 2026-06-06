-- Ensure AI ops tables exist in databases that already have a higher Flyway version.

CREATE TABLE IF NOT EXISTS ai_model_version (
    id VARCHAR(36) PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    version VARCHAR(50) NOT NULL,
    training_status VARCHAR(50) NOT NULL,
    trained_at TIMESTAMP,
    metrics TEXT,
    artifact_path VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_ai_model_version UNIQUE (model_name, version)
);

CREATE TABLE IF NOT EXISTS ai_training_interaction (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    ho_so_ai_id VARCHAR(36),
    asset_id VARCHAR(255),
    asset_type VARCHAR(50),
    event_type VARCHAR(50) NOT NULL,
    label_weight NUMERIC(5, 2) NOT NULL DEFAULT 0,
    context_city VARCHAR(255),
    context_weather VARCHAR(50),
    source VARCHAR(100),
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ai_item_embedding (
    id VARCHAR(36) PRIMARY KEY,
    asset_id VARCHAR(255) NOT NULL,
    asset_type VARCHAR(50) NOT NULL,
    embedding TEXT NOT NULL,
    feature_summary TEXT,
    model_name VARCHAR(100) NOT NULL DEFAULT 'two_tower_feature_hashing',
    model_version VARCHAR(50) NOT NULL DEFAULT 'v1-feature-hash',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_ai_item_embedding UNIQUE (asset_id, asset_type, model_name, model_version)
);

CREATE TABLE IF NOT EXISTS ai_partner_profile (
    id VARCHAR(36) PRIMARY KEY,
    partner_id VARCHAR(36) NOT NULL UNIQUE,
    business_profile_id VARCHAR(36),
    profile_summary TEXT,
    health_score INTEGER NOT NULL DEFAULT 0,
    revenue_trend VARCHAR(50),
    occupancy_trend VARCHAR(50),
    review_trend VARCHAR(50),
    model_version VARCHAR(50) NOT NULL DEFAULT 'partner-insight-v1',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ai_partner_insight (
    id VARCHAR(36) PRIMARY KEY,
    partner_id VARCHAR(36) NOT NULL,
    business_profile_id VARCHAR(36),
    insight_type VARCHAR(50) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    recommended_action VARCHAR(1000),
    metric_name VARCHAR(100),
    metric_value NUMERIC(18, 2),
    model_version VARCHAR(50) NOT NULL DEFAULT 'partner-insight-v1',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE ai_partner_insight ADD COLUMN IF NOT EXISTS partner_id VARCHAR(36);
ALTER TABLE ai_partner_insight ADD COLUMN IF NOT EXISTS business_profile_id VARCHAR(36);
ALTER TABLE ai_partner_insight ADD COLUMN IF NOT EXISTS insight_type VARCHAR(50);
ALTER TABLE ai_partner_insight ADD COLUMN IF NOT EXISTS severity VARCHAR(50);
ALTER TABLE ai_partner_insight ADD COLUMN IF NOT EXISTS message VARCHAR(1000);
ALTER TABLE ai_partner_insight ADD COLUMN IF NOT EXISTS recommended_action VARCHAR(1000);
ALTER TABLE ai_partner_insight ADD COLUMN IF NOT EXISTS model_version VARCHAR(50) DEFAULT 'partner-insight-v1';

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'ai_partner_insight'
          AND column_name = 'doi_tac_id'
    ) THEN
        EXECUTE 'UPDATE ai_partner_insight SET partner_id = COALESCE(partner_id, doi_tac_id) WHERE partner_id IS NULL';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'ai_partner_insight'
          AND column_name = 'ho_so_kinh_doanh_id'
    ) THEN
        EXECUTE 'UPDATE ai_partner_insight SET business_profile_id = COALESCE(business_profile_id, ho_so_kinh_doanh_id) WHERE business_profile_id IS NULL';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'ai_partner_insight'
          AND column_name = 'description'
    ) THEN
        EXECUTE 'UPDATE ai_partner_insight SET message = COALESCE(message, description) WHERE message IS NULL';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'ai_partner_insight'
          AND column_name = 'action'
    ) THEN
        EXECUTE 'UPDATE ai_partner_insight SET recommended_action = COALESCE(recommended_action, action) WHERE recommended_action IS NULL';
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ai_training_interaction_user') THEN
        ALTER TABLE ai_training_interaction
            ADD CONSTRAINT fk_ai_training_interaction_user
            FOREIGN KEY (user_id) REFERENCES khach_hang(id) ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ai_training_interaction_profile') THEN
        ALTER TABLE ai_training_interaction
            ADD CONSTRAINT fk_ai_training_interaction_profile
            FOREIGN KEY (ho_so_ai_id) REFERENCES ho_so_ai(id_ho_so_ai) ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ai_training_interaction_asset') THEN
        ALTER TABLE ai_training_interaction
            ADD CONSTRAINT fk_ai_training_interaction_asset
            FOREIGN KEY (asset_id) REFERENCES tai_san(id_tai_san) ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ai_item_embedding_asset') THEN
        ALTER TABLE ai_item_embedding
            ADD CONSTRAINT fk_ai_item_embedding_asset
            FOREIGN KEY (asset_id) REFERENCES tai_san(id_tai_san) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ai_partner_profile_partner') THEN
        ALTER TABLE ai_partner_profile
            ADD CONSTRAINT fk_ai_partner_profile_partner
            FOREIGN KEY (partner_id) REFERENCES doi_tac(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ai_partner_profile_business') THEN
        ALTER TABLE ai_partner_profile
            ADD CONSTRAINT fk_ai_partner_profile_business
            FOREIGN KEY (business_profile_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ai_partner_insight_partner') THEN
        ALTER TABLE ai_partner_insight
            ADD CONSTRAINT fk_ai_partner_insight_partner
            FOREIGN KEY (partner_id) REFERENCES doi_tac(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ai_partner_insight_business') THEN
        ALTER TABLE ai_partner_insight
            ADD CONSTRAINT fk_ai_partner_insight_business
            FOREIGN KEY (business_profile_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE SET NULL;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_ai_training_user_time ON ai_training_interaction(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_ai_training_asset_event ON ai_training_interaction(asset_id, event_type);
CREATE INDEX IF NOT EXISTS idx_ai_embedding_asset ON ai_item_embedding(asset_id, asset_type);
CREATE INDEX IF NOT EXISTS idx_ai_partner_insight_partner ON ai_partner_insight(partner_id, created_at DESC);

INSERT INTO ai_model_version (
    id, model_name, version, training_status, trained_at, metrics, artifact_path
) VALUES (
    'model-two-tower-feature-hash-v1',
    'two_tower_feature_hashing',
    'v1-feature-hash',
    'SERVING_DEMO',
    CURRENT_TIMESTAMP,
    '{"embedding_size":32,"source":"feature_hashing","note":"Ready for demo serving; replace artifact_path after offline training."}',
    'classpath:/ai/two-tower-feature-hash'
) ON CONFLICT (model_name, version) DO UPDATE SET
    training_status = EXCLUDED.training_status,
    trained_at = EXCLUDED.trained_at,
    metrics = EXCLUDED.metrics,
    artifact_path = EXCLUDED.artifact_path,
    updated_at = CURRENT_TIMESTAMP;
