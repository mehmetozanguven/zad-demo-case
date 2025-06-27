--- for spring modulith
CREATE TABLE IF NOT EXISTS event_publication
(
  id               UUID NOT NULL,
  listener_id      TEXT NOT NULL,
  event_type       TEXT NOT NULL,
  serialized_event TEXT NOT NULL,
  publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
  completion_date  TIMESTAMP WITH TIME ZONE,
  PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS event_publication_serialized_event_hash_idx ON event_publication USING hash(serialized_event);
CREATE INDEX IF NOT EXISTS event_publication_by_completion_date_idx ON event_publication (completion_date);

--- for shedlock
CREATE TABLE IF NOT EXISTS shedlock (
    name VARCHAR(64) NOT NULL,
    lock_until TIMESTAMP WITH TIME ZONE NOT NULL,
    locked_at TIMESTAMP WITH TIME ZONE NOT NULL,
    locked_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);



CREATE TABLE users (
    email character varying(100) UNIQUE,
    id character varying(60) PRIMARY KEY NOT NULL,
    created_date timestamp(6) with time zone,
    created_date_in_ms BIGINT NOT NULL,
    created_date_offset smallint,
    last_update_date timestamp(6) with time zone,
    last_update_date_in_ms BIGINT NOT NULL,
    last_update_offset smallint,
    entity_version_id bigint,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE accounts (
    account_type character varying(60),
    currency_type character varying(60),
    user_id character varying(60),
    balance NUMERIC(19, 4),
    id character varying(60) PRIMARY KEY NOT NULL,
    created_date timestamp(6) with time zone,
    created_date_in_ms BIGINT NOT NULL,
    created_date_offset smallint,
    last_update_date timestamp(6) with time zone,
    last_update_date_in_ms BIGINT NOT NULL,
    last_update_offset smallint,
    entity_version_id bigint,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE account_transactions (
    account_id character varying(60),
    transaction_type character varying(60),
    transaction_status character varying(60),
    amount NUMERIC(19, 4),
    currency_type character varying(60),
    processed_date timestamp(6) with time zone,
    processed_date_offset smallint,
    expiration_time timestamp(6) with time zone,
    expiration_time_offset smallint,
    info_field jsonb,
    id character varying(60) PRIMARY KEY NOT NULL,
    created_date timestamp(6) with time zone,
    created_date_in_ms BIGINT NOT NULL,
    created_date_offset smallint,
    last_update_date timestamp(6) with time zone,
    last_update_date_in_ms BIGINT NOT NULL,
    last_update_offset smallint,
    entity_version_id bigint,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE exchanges (
    exchange_type character varying(60),
    exchange_rate NUMERIC(19, 4),
    expiration_time timestamp(6) with time zone NOT NULL,
    expiration_time_offset smallint NOT NULL,
    id character varying(60) PRIMARY KEY NOT NULL,
    created_date timestamp(6) with time zone,
    created_date_in_ms BIGINT NOT NULL,
    created_date_offset smallint,
    last_update_date timestamp(6) with time zone,
    last_update_date_in_ms BIGINT NOT NULL,
    last_update_offset smallint,
    entity_version_id bigint,
    active BOOLEAN DEFAULT TRUE
);


