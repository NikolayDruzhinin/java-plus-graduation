CREATE ROLE postgres      LOGIN PASSWORD '12345';

CREATE DATABASE "statsdb";
CREATE DATABASE "maindb";
CREATE DATABASE "userdb";
CREATE DATABASE "requestdb";
CREATE DATABASE "eventdb";

ALTER DATABASE "statsdb" OWNER TO postgres;
ALTER DATABASE "maindb" OWNER TO postgres;
ALTER DATABASE "userdb" OWNER TO postgres;
ALTER DATABASE "requestdb" OWNER TO postgres;
ALTER DATABASE "eventdb" OWNER TO postgres;
