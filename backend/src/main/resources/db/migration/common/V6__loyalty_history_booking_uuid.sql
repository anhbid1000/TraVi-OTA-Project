-- Align loyalty point history with UUID booking identifiers used by don_dat_cho.
ALTER TABLE lich_su_diem
    ALTER COLUMN booking_id TYPE VARCHAR(36)
    USING booking_id::TEXT;
