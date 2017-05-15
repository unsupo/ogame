-- SET SEARCH_PATH TO OGAME;

-- DROP TABLE SERVER CASCADE;
-- DROP TABLE ALLIANCE CASCADE;
-- DROP TABLE PLAYER CASCADE;
-- DROP TABLE PLANET CASCADE;
-- DROP TABLE ALLIANCE_HIGHSCORE CASCADE;
-- DROP TABLE PLAYER_HIGHSCORE CASCADE;

------------------------------------------------------------------------------------------------------------------------
------------------------------- XML API TABLES
------------------------------------------------------------------------------------------------------------------------


CREATE TABLE IF NOT EXISTS SERVER(
  SERVER_ID                       INTEGER NOT NULL,
  SERVER_NAME                     VARCHAR(20),
  LANGUAGE                        CHAR(2),
  TIMEZONE                        VARCHAR(15),
  TIMEZONE_OFFSET                 CHAR(6),
  DOMAIN                          VARCHAR(30),
  VERSION                         VARCHAR(20),
  SPEED                           smallint,
  SPEED_FLEET                     smallint,
  GALAXIES                        smallint,
  SYSTEMS                         smallint,
  ACS                             smallint,
  RAPIDFIRE                       smallint,
  DEFTOTF                         smallint,
  DEBRIS_FACTOR                   decimal,
  DEBRIS_FACTOR_DEF               decimal,
  REPAIR_FACTOR                   decimal,
  NEWBIE_PROTECTION_LIMIT         integer,
  NEWBIE_PROTECTION_HIGH          integer,
  TOP_SCORE                       bigint,
  BONUS_FIELDS                    smallint,
  DONUT_GALAXY                    smallint,
  DONUT_SYSTEM                    smallint,
  WF_ENABLED                      INTEGER,
  WF_MINIMUM_RESS_LOST            INTEGER,
  WF_MINIMUM_LOSS_PERCENTAGE      smallint,
  WF_BASIC_PERCENTAGE_REPAIRABLE  smallint,
  GLOBAL_DEUTERIUM_SAVE_FACTOR    DECIMAL,
  TIMESTAMP                       BIGINT NOT NULL,--timestamp default current_timestamp,
  PRIMARY KEY (SERVER_ID,TIMESTAMP)
);


CREATE TABLE IF NOT EXISTS ALLIANCE(
  ALLIANCE_ID INTEGER NOT NULL,
  SERVER_ID   INTEGER,
  NAME        VARCHAR(100),
  TAG         VARCHAR(20),
  HOMEPAGE    VARCHAR(250),
  LOGO        VARCHAR(250),
  OPEN        CHAR(1),
  TIMESTAMP   BIGINT,
  SERVER_T    BIGINT,
  PRIMARY KEY (ALLIANCE_ID,SERVER_ID,TIMESTAMP),
  FOREIGN KEY (SERVER_ID,SERVER_T) REFERENCES SERVER(SERVER_ID,TIMESTAMP)
);

CREATE TABLE IF NOT EXISTS PLAYER(
  PLAYER_ID   INTEGER NOT NULL,
  SERVER_ID   INTEGER,
  NAME        VARCHAR(100),
  STATUS      VARCHAR(3),
  ALLIANCE_ID INTEGER,-- REFERENCES ALLIANCE(ALLIANCE_ID),
  TIMESTAMP                       BIGINT,
  AllIANCE_T  BIGINT,
  PRIMARY KEY (PLAYER_ID,SERVER_ID,TIMESTAMP),
  FOREIGN KEY (ALLIANCE_ID,AllIANCE_T,SERVER_ID) REFERENCES ALLIANCE(ALLIANCE_ID,TIMESTAMP,SERVER_ID)
);

CREATE TABLE IF NOT EXISTS PLANET(
  PLANET_ID     INTEGER NOT NULL,
  SERVER_ID     INTEGER,
  PLAYER_ID     INTEGER,
  NAME          VARCHAR(100),
  COORDS        VARCHAR(8),
  MOON_ID       INTEGER,
  MOON_NAME     VARCHAR(100),
  MOON_SIZE     INTEGER,
  TIMESTAMP     BIGINT,
  PLAYER_T      BIGINT,
  PRIMARY KEY (PLANET_ID,SERVER_ID,TIMESTAMP),
  FOREIGN KEY (PLAYER_ID,PLAYER_T,SERVER_ID) REFERENCES PLAYER(PLAYER_ID,TIMESTAMP,SERVER_ID)
);

/*
TYPES
0 Total
1 Economy
2	Research
3	Military
5	Military Built
6	Military Destroyed
4	Military Lost
7	Honor
 */
CREATE TABLE IF NOT EXISTS PLAYER_HIGHSCORE(
  PLAYER_ID   INTEGER,
  SERVER_ID   INTEGER,
  POSITION    INTEGER,
  SCORE       BIGINT,
  TYPE        CHAR(1), --SEE TABLE ABOVE
  TIMESTAMP                       BIGINT,
  PLAYER_T    BIGINT,
  PRIMARY KEY (PLAYER_ID,SERVER_ID,TIMESTAMP),
  FOREIGN KEY (PLAYER_ID,SERVER_ID,PLAYER_T) REFERENCES PLAYER(PLAYER_ID,SERVER_ID,TIMESTAMP)
);
CREATE TABLE IF NOT EXISTS ALLIANCE_HIGHSCORE(
  ALLIANCE_ID INTEGER,
  SERVER_ID   INTEGER,
  POSITION    INTEGER,
  SCORE       BIGINT,
  TYPE        CHAR(1), --SEE TABLE ABOVE
  TIMESTAMP                       BIGINT,
  ALLIANCE_T  BIGINT,
  PRIMARY KEY (ALLIANCE_ID,SERVER_ID,TIMESTAMP),
  FOREIGN KEY (ALLIANCE_ID,SERVER_ID,ALLIANCE_T) REFERENCES ALLIANCE(ALLIANCE_ID,SERVER_ID,TIMESTAMP)
);



------------------------------------------------------------------------------------------------------------------------
------------------------------- USER TABLES
------------------------------------------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS USERS(
  id          SERIAL PRIMARY KEY,
  USERNAME    VARCHAR(100) NOT NULL UNIQUE,
  PASSWORD    VARCHAR(100) NOT NULL,
  FIRST_NAME  VARCHAR(100) NOT NULL,
  LAST_NAME   VARCHAR(100) NOT NULL,
  USER_TYPE   CHAR(1) DEFAULT 'N' CHECK(USER_TYPE in ('A'/*ADMIN*/, 'N' /*Normal User*/)),
  ACTIVE      CHAR(1) DEFAULT 'A' CHECK(ACTIVE in ('A'/*ADMIN*/, 'N' /*Normal User*/))
);

CREATE TABLE IF NOT EXISTS TOKENS(
  USERS_ID          INTEGER REFERENCES USERS(id) PRIMARY KEY,
  TOKEN             VARCHAR(100) NOT NULL UNIQUE,
  TIMESTAMP         timestamp DEFAULT current_timestamp,
  EXPIRE_TIMESTAMP  TIMESTAMP DEFAULT current_timestamp + (1 || ' days')::INTERVAL
);