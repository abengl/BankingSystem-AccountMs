-- ============================================================
-- V2__seed_account_data.sql
-- Inserts initial account records.
-- Mirrors the DataLoader bootstrap data.
-- customer_id values reference the customers seeded in
-- customer-ms V2__seed_customer_data.sql (IDs 1-7).
-- ============================================================

INSERT INTO account (account_number, balance, account_type, customer_id, creation_date, update_date, active)
VALUES
    ('A00000000001', 1000.0, 'SAVINGS',  1, NOW(), NOW(), 1),
    ('A00000000002', 1000.0, 'CHECKING', 1, NOW(), NOW(), 1),
    ('A00000000003', 1000.0, 'SAVINGS',  2, NOW(), NOW(), 1),
    ('A00000000004', 1000.0, 'CHECKING', 2, NOW(), NOW(), 1),
    ('A00000000005', 1000.0, 'SAVINGS',  3, NOW(), NOW(), 1),
    ('A00000000006', 1000.0, 'CHECKING', 3, NOW(), NOW(), 1),
    ('A00000000007', 1000.0, 'CHECKING', 4, NOW(), NOW(), 1),
    ('A00000000008', 1000.0, 'SAVINGS',  4, NOW(), NOW(), 1);