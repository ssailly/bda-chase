DROP TABLE IF EXISTS R1;
DROP TABLE IF EXISTS R2;
DROP TABLE IF EXISTS R3;
DROP TABLE IF EXISTS P1;
DROP TABLE IF EXISTS P2;
DROP TABLE IF EXISTS P3;
DROP TABLE IF EXISTS Q1;
DROP TABLE IF EXISTS Q2;
DROP TABLE IF EXISTS Q3;

CREATE TABLE R1 (A VARCHAR(255), B VARCHAR(255));
CREATE TABLE R2 (A VARCHAR(255), B VARCHAR(255));
CREATE TABLE R3 (A VARCHAR(255), B VARCHAR(255));

CREATE TABLE P1 (C VARCHAR(255), D VARCHAR(255));
CREATE TABLE P2 (C VARCHAR(255), D VARCHAR(255));
CREATE TABLE P3 (C VARCHAR(255), D VARCHAR(255));

CREATE TABLE Q1 (E VARCHAR(255), F VARCHAR(255), G VARCHAR(255));
CREATE TABLE Q2 (E VARCHAR(255), F VARCHAR(255), G VARCHAR(255));
CREATE TABLE Q3 (E VARCHAR(255), F VARCHAR(255), G VARCHAR(255));

INSERT INTO R1 VALUES ('a', 'b');
INSERT INTO P2 VALUES ('c', 'd');
INSERT INTO Q3 VALUES ('e', 'f', 'g');