-- SET SEARCH_PATH TO OGAME;

CREATE TABLE IF NOT EXISTS SERVER(
  SERVER_ID                       INTEGER PRIMARY KEY,
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
  GLOBAL_DEUTERIUM_SAVE_FACTOR    smallint,
  timestamp   timestamp default current_timestamp
);

CREATE TABLE IF NOT EXISTS ALLIANCE(
  ALLIANCE_ID INTEGER PRIMARY KEY,
  SERVER_ID   INTEGER REFERENCES SERVER(SERVER_ID),
  NAME        VARCHAR(100),
  TAG         VARCHAR(20),
  HOMEPAGE    VARCHAR(250),
  LOGO        VARCHAR(250),
  OPEN        CHAR(1),
  timestamp   timestamp default current_timestamp
);

CREATE TABLE IF NOT EXISTS PLAYER(
  PLAYER_ID   INTEGER PRIMARY KEY,
  SERVER_ID INTEGER REFERENCES SERVER(SERVER_ID),
  NAME        VARCHAR(100),
  STATUS      VARCHAR(3),
  ALLIANCE_ID INTEGER REFERENCES ALLIANCE(ALLIANCE_ID),
  timestamp   timestamp default current_timestamp
);

-- CREATE TABLE IF NOT EXISTS MOON(
--   MOON_ID INTEGER PRIMARY KEY,
--   SERVER_ID INTEGER REFERENCES SERVER(SERVER_ID),
--   NAME    VARCHAR(100),
--   SIZE    INTEGER,
--   timestamp   timestamp default current_timestamp
-- );

CREATE TABLE IF NOT EXISTS PLANET(
  PLANET_ID     INTEGER PRIMARY KEY,
  SERVER_ID     INTEGER REFERENCES SERVER(SERVER_ID),
  PLAYER_ID     INTEGER REFERENCES PLAYER(PLAYER_ID),
  NAME          VARCHAR(100),
  COORDS        VARCHAR(8),
  MOON_ID       INTEGER, -- REFERENCES MOON(MOON_ID)
  MOON_NAME     VARCHAR(100),
  MOON_SIZE     INTEGER,
  timestamp     timestamp default current_timestamp
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
  PLAYER_ID   INTEGER REFERENCES PLAYER(PLAYER_ID),
  SERVER_ID   INTEGER REFERENCES SERVER(SERVER_ID),
  POSITION    INTEGER,
  SCORE       BIGINT,
  TYPE        CHAR(1), --SEE TABLE ABOVE
  timestamp   timestamp default current_timestamp
);
CREATE TABLE IF NOT EXISTS ALLIANCE_HIGHSCORE(
  ALLIANCE_ID INTEGER REFERENCES ALLIANCE(ALLIANCE_ID),
  SERVER_ID   INTEGER REFERENCES SERVER(SERVER_ID),
  POSITION    INTEGER,
  SCORE       BIGINT,
  TYPE        CHAR(1), --SEE TABLE ABOVE
  timestamp   timestamp default current_timestamp
);