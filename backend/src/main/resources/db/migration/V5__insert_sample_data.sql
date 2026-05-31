-- =============================================================
-- V5__insert_sample_data.sql
-- NOTE (2026-05-31):
-- Bo migration seed du lieu legacy de tranh xung dot schema giua cac nhanh/phien ban.
-- Neu can seed du lieu dev, thuc hien bang script rieng trong db/seed.
-- =============================================================

DO $$
BEGIN
  RAISE NOTICE 'Skip V5 sample data migration (legacy seed disabled).';
END
$$;
