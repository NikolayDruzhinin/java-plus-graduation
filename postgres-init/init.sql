CREATE ROLE main_user      LOGIN PASSWORD 'password';
CREATE ROLE stat_user      LOGIN PASSWORD 'password';

CREATE DATABASE "main";
CREATE DATABASE "stat";

ALTER DATABASE "main"      OWNER TO main_user;
ALTER DATABASE "stat"      OWNER TO stat_user;
