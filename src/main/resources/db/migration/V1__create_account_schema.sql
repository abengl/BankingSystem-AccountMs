-- ============================================================
-- V1__create_account_schema.sql
-- Creates the account table schema for the Account MS.
-- Replaces JPA ddl-auto schema generation.
-- account_type stores the enum as a VARCHAR string (EnumType.STRING).
-- ============================================================

CREATE TABLE IF NOT EXISTS account
(
    account_id     INT         NOT NULL AUTO_INCREMENT,
    account_number VARCHAR(30) NOT NULL,
    balance        DOUBLE      NOT NULL,
    account_type   VARCHAR(20) NOT NULL,
    customer_id    INT         NOT NULL,
    creation_date  DATETIME    NOT NULL,
    update_date    DATETIME    NOT NULL,
    active         TINYINT(1)  NOT NULL DEFAULT 1,

    CONSTRAINT pk_account PRIMARY KEY (account_id),
    CONSTRAINT uq_account_number UNIQUE (account_number),
    CONSTRAINT chk_account_type CHECK (account_type IN ('SAVINGS', 'CHECKING')),
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0)
);