DROP TABLE IF EXISTS pizza_order CASCADE;

DROP TABLESPACE IF EXISTS usa_ts;

DROP TABLESPACE IF EXISTS europe_ts;

DROP TABLESPACE IF EXISTS australia_ts;

DROP CAST IF EXISTS (varchar AS order_status);

DROP CAST IF EXISTS (varchar AS store_location);

DROP TYPE IF EXISTS order_status;

DROP TYPE IF EXISTS store_location;

