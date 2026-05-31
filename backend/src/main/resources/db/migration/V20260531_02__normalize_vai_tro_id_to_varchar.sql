-- Chuan hoa kieu ID cua bang vai_tro ve VARCHAR de khop voi entity VaiTro (String).
-- Dung dieu kien de chay an toan tren ca DB cu (id integer) va DB moi (id varchar).
DO
$$
DECLARE
    v_vai_tro_id_type    TEXT;
    v_users_vai_tro_type TEXT;
    v_fk_name            TEXT;
BEGIN
    SELECT c.data_type
    INTO v_vai_tro_id_type
    FROM information_schema.columns c
    WHERE c.table_schema = 'public'
      AND c.table_name = 'vai_tro'
      AND c.column_name = 'id';

    SELECT c.data_type
    INTO v_users_vai_tro_type
    FROM information_schema.columns c
    WHERE c.table_schema = 'public'
      AND c.table_name = 'users'
      AND c.column_name = 'vai_tro_id';

    IF v_vai_tro_id_type IS NULL OR v_users_vai_tro_type IS NULL THEN
        RAISE NOTICE 'Skip normalize vai_tro id: missing table/column.';
    ELSIF v_vai_tro_id_type IN ('smallint', 'integer', 'bigint', 'numeric')
        OR v_users_vai_tro_type IN ('smallint', 'integer', 'bigint', 'numeric') THEN

        -- Drop toan bo FK dang tro tu users.vai_tro_id -> vai_tro.id de doi kieu cot.
        FOR v_fk_name IN
            SELECT tc.constraint_name
            FROM information_schema.table_constraints tc
                     JOIN information_schema.key_column_usage kcu
                          ON tc.constraint_name = kcu.constraint_name
                              AND tc.table_schema = kcu.table_schema
            WHERE tc.table_schema = 'public'
              AND tc.table_name = 'users'
              AND tc.constraint_type = 'FOREIGN KEY'
              AND kcu.column_name = 'vai_tro_id'
            LOOP
                EXECUTE format('ALTER TABLE public.users DROP CONSTRAINT %I', v_fk_name);
            END LOOP;

        IF v_vai_tro_id_type IN ('smallint', 'integer', 'bigint', 'numeric') THEN
            EXECUTE 'ALTER TABLE public.vai_tro ALTER COLUMN id DROP IDENTITY IF EXISTS';
            EXECUTE 'ALTER TABLE public.vai_tro ALTER COLUMN id DROP DEFAULT';
            EXECUTE 'ALTER TABLE public.vai_tro ALTER COLUMN id TYPE VARCHAR(36) USING id::text';
        END IF;

        IF v_users_vai_tro_type IN ('smallint', 'integer', 'bigint', 'numeric') THEN
            EXECUTE 'ALTER TABLE public.users ALTER COLUMN vai_tro_id TYPE VARCHAR(36) USING vai_tro_id::text';
        END IF;

        -- Tao lai FK voi ten on dinh de tranh le thuoc ten random cua Hibernate.
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.table_constraints tc
                       WHERE tc.table_schema = 'public'
                         AND tc.table_name = 'users'
                         AND tc.constraint_name = 'fk_users_vai_tro_id') THEN
            EXECUTE 'ALTER TABLE public.users
                     ADD CONSTRAINT fk_users_vai_tro_id
                     FOREIGN KEY (vai_tro_id) REFERENCES public.vai_tro(id) ON DELETE SET NULL';
        END IF;
    END IF;
END
$$;
