(ns migrations.001-base-schema
  (:require
    [faves-api.models 
      [industry :as i] 
      [industry-group :as ig] 
      [industry-sub-sector :as iss] 
      [industry-sector :as is]])
  (:use 
    [korma core db] 
    [clojure.java io]))

(defn up []
  (transaction


   (exec-raw (str "CREATE EXTENSION IF NOT EXISTS postgis"))

   (exec-raw (str "CREATE OR REPLACE FUNCTION update_created_at_column() "
                  "RETURNS TRIGGER AS ' "
                  "BEGIN "
                    "NEW.updated_at = NOW(); "
                    "RETURN NEW; "
                  "END; "
                  "' LANGUAGE 'plpgsql';"))

   (exec-raw (str "create table if not exists users ("
                  "id serial PRIMARY KEY,"
                  "email character varying(255) NOT NULL DEFAULT ''::character varying,"
                  "reset_password_token character varying(255),"
                  "reset_password_sent_at timestamp without time zone,"
                  "password character varying(255),"
                  "sign_in_count integer DEFAULT 0,"
                  "current_sign_in_at timestamp without time zone,"
                  "last_sign_in_at timestamp without time zone,"
                  "current_sign_in_ip character varying(255),"
                  "last_sign_in_ip character varying(255),"
                  "created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "username character varying(255),"
                  "firstname character varying(255),"
                  "lastname character varying(255),"
                  "confirmation_token character varying(255),"
                  "confirmed_at timestamp without time zone,"
                  "confirmation_sent_at timestamp without time zone,"
                  "access_token character varying(255),"
                  "gender character varying(255) default '',"
                  "birthdate date,"
                  "unlock_token character varying(255),"
                  "locked_at timestamp without time zone,"
                  "verified_status integer NOT NULL DEFAULT 0"
                  ")"))

   (exec-raw (str "create table if not exists roles ("
                  "id serial PRIMARY KEY,"
                  "name character varying(255)"
                  ")"))


   (exec-raw (str "create table if not exists industry_sectors ("
                  "id character varying(2),"
                  "name character varying(256),"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP"
                  ")"))

    (exec-raw (str "CREATE UNIQUE INDEX index_industry_sectors_id"
                   " ON industry_sectors"
                   " USING btree"
                   " (id COLLATE pg_catalog.\"default\")"))

   (exec-raw (str "create table if not exists industry_sub_sectors ("
                  "id character varying(3),"
                  "name character varying(256),"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "industry_sectors_id character varying(2) references industry_sectors(id)"
                  ")"))

    (exec-raw (str "CREATE UNIQUE INDEX index_industry_sub_sectors_id"
                   " ON industry_sub_sectors"
                   " USING btree"
                   " (id COLLATE pg_catalog.\"default\")"))

   (exec-raw (str "create table if not exists industry_groups ("
                  "id character varying(4),"
                  "name character varying(256),"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "industry_sub_sectors_id character varying(3) references industry_sub_sectors(id)"
                  ")"))

    (exec-raw (str "CREATE UNIQUE INDEX index_industry_groups_id"
                   " ON industry_groups"
                   " USING btree"
                   " (id COLLATE pg_catalog.\"default\")"))

   (exec-raw (str "create table if not exists industries ("
                  "id character varying(5),"
                  "name character varying(256),"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "industry_groups_id character varying(4) references industry_groups(id)"
                  ")"))

    (exec-raw (str "CREATE UNIQUE INDEX index_industries_id"
                   " ON industries"
                   " USING btree"
                   " (id COLLATE pg_catalog.\"default\")"))

   (exec-raw (str "create table if not exists organizations ("
                  "id SERIAL PRIMARY KEY,"
                  "created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "name TEXT NOT NULL,"
                  "users_id INT REFERENCES users(id),"
                  "industries_id character varying(6) REFERENCES industries(id)"
                  ")"))

   (exec-raw (str "create table if not exists memberships ("
                  "id serial PRIMARY KEY,"
                  "users_id integer references users(id),"
                  "roles_id integer references roles(id),"
                  "organizations_id integer references organizations(id),"
                  "created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP"
                  ")"))

   (exec-raw (str "create table if not exists authentications ("
                  "id serial PRIMARY KEY,"
                  "users_id integer references users(id),"
                  "organizations_id integer,"
                  "provider_uuid character varying(255),"
                  "provider character varying(255),"
                  "created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "access_token text,"
                  "username character varying(255),"
                  "token_secret character varying(255),"
                  "device_id character varying(255),"
                  "device_token character varying(255),"
                  "device_type character varying(255),"
                  "client_version character varying(255),"
                  "user_agent character varying(255),"
                  "remote_addr character varying(255)"
                  ")"))

   (exec-raw (str "create table if not exists relationship_types ("
                  "id serial PRIMARY KEY,"
                  "name character varying(255)"
                  ")"))

   (exec-raw (str "create table if not exists entity_types ("
                  "id serial PRIMARY KEY,"
                  "name character varying(255),"
                  "created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP"
                  ")"))

   (exec-raw (str "create table if not exists relationships ("
                  "id serial PRIMARY KEY,"
                  "users_id integer references users(id),"
                  "relationship_types_id integer references relationship_types(id),"
                  "entity_types_id integer references entity_types(id),"
                  "entity_id integer not null,"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP"
                  ")"))

   (exec-raw (str "create table if not exists street_numbers ("
                  "id integer PRIMARY KEY"
                  ")"))

   (exec-raw (str "create table if not exists street_names ("
                  "id SERIAL PRIMARY KEY,"
                  "name character varying(500)"
                  ")"))

   (exec-raw (str "create table if not exists street_suffixes ("
                  "id serial PRIMARY KEY,"
                  "suffix character varying(5)"
                  ")"))

   (exec-raw (str "create table if not exists street_types ("
                  "id SERIAL PRIMARY KEY,"
                  "type character varying(256)"
                  ")"))

   (exec-raw (str "create table if not exists address_unit_identifiers ("
                  "id serial primary key,"
                  "name character varying(256)"
                  ")"))

   (exec-raw (str "create table if not exists directions ("
                  "id SERIAL PRIMARY KEY,"
                  "name character varying(25),"
                  "abbrev_1 character varying(1),"
                  "abbrev_2 character varying(2)"
                  ")"))

   (exec-raw (str "create table if not exists locations ("
                  "id serial PRIMARY KEY,"
                  "organizations_id INTEGER REFERENCES organizations(id),"
                  "point geometry(POINT,4326)"
                  ")"))

   (exec-raw (str "create table if not exists local_municipalities ("
                  "id SERIAL PRIMARY KEY,"
                  "name character varying(256)"
                  ")"))

   (exec-raw (str "create table if not exists cities ("
                  "id SERIAL PRIMARY KEY,"
                  "name character varying(256),"
                  "point geometry(POINT,4326)"
                  ")"))

   (exec-raw (str "create table if not exists district_types ("
                  "id serial primary key,"
                  "name character varying(255)"
                  ")"))

   (exec-raw (str "create table if not exists districts ("
                  "id SERIAL PRIMARY KEY,"
                  "name character varying(300),"
                  "district_type integer references districts(id),"
                  "point geometry(POINT,4326)"
                  ")"))

   (exec-raw (str "create table if not exists postal_codes ("
                  "id serial primary key,"
                  "name character varying(256)"
                  ")"))

   (exec-raw (str "create table if not exists continents ("
                  "id SERIAL PRIMARY KEY,"
                  "name character varying(256),"
                  "iso_2 character varying(2),"
                  "iso_3 character varying(3),"
                  "point geometry(POINT, 4326)"
                  ")"))

   (exec-raw (str "create table if not exists countries ("
                  "id serial primary key,"
                  "name character varying(256),"
                  "iso_2 character varying(2),"
                  "iso_3 character varying(3),"
                  "point geometry(POINT,4326),"
                  "continents_id integer references continents(id)"
                  ")"))

   (exec-raw (str "create table if not exists favors ("
                  "id SERIAL PRIMARY KEY,"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "title TEXT NOT NULL,"
                  "instructions TEXT NOT NULL,"
                  "users_id INT REFERENCES users(id)"
                  ")"))

   (exec-raw (str "create table if not exists favor_list_items ("
                  "id SERIAL PRIMARY KEY,"
                  "favors_id INT REFERENCES favors(id),"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "title TEXT NOT NULL,"
                  "instructions TEXT NOT NULL,"
                  "users_id INT REFERENCES users(id)"
                  ")"))

   (exec-raw (str "create table if not exists auctions ("
                  "id SERIAL PRIMARY KEY,"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "favors_id INT REFERENCES favors(id)"
                  ")"))



    (exec-raw (str "CREATE INDEX index_users_on_password"
                   " ON users"
                   " USING btree"
                   " (password COLLATE pg_catalog.\"default\")"))

    (exec-raw (str "CREATE UNIQUE INDEX index_users_on_confirmation_token"
                   " ON users"
                   " USING btree"
                   " (confirmation_token COLLATE pg_catalog.\"default\")"))

    (exec-raw (str "CREATE INDEX index_users_on_created_at"
                   " ON users"
                   " USING btree"
                   " (created_at)"))

    (exec-raw (str "CREATE UNIQUE INDEX index_users_on_email"
                   " ON users"
                   " USING btree"
                   " (email COLLATE pg_catalog.\"default\")"))

    (exec-raw (str "CREATE UNIQUE INDEX index_users_on_reset_password_token"
                   " ON users"
                   " USING btree"
                   " (reset_password_token COLLATE pg_catalog.\"default\")"))

    (exec-raw (str "CREATE UNIQUE INDEX index_users_on_username"
                   " ON users"
                   " USING btree"
                   " (username COLLATE pg_catalog.\"default\")"))

    (exec-raw (str "CREATE UNIQUE INDEX index_users_on_username_lowercase"
                   " ON users"
                   " USING btree"
                   " (lower(username::text) COLLATE pg_catalog.\"default\")"))

    (exec-raw (str "CREATE INDEX index_authentications_on_id_and_access_token_and_provider"
                    " ON authentications"
                    " USING btree"
                    " (id, access_token COLLATE pg_catalog.\"default\", provider COLLATE pg_catalog.\"default\")"))

    (exec-raw (str "CREATE INDEX index_authentications_on_users_id"
                    " ON authentications"
                    " USING btree"
                    " (users_id)"))

    (exec-raw (str "CREATE INDEX index_authentications_on_access_token"
                    " ON authentications"
                    " USING btree"
                    " (access_token COLLATE pg_catalog.\"default\")"))

    (exec-raw (str "CREATE TRIGGER update_update_at_modtime BEFORE UPDATE "
                   "ON users FOR EACH ROW EXECUTE PROCEDURE "
                   "update_created_at_column();"))

    (exec-raw (str "CREATE TRIGGER update_update_at_modtime BEFORE UPDATE "
                   "ON authentications FOR EACH ROW EXECUTE PROCEDURE "
                   "update_created_at_column();"))

    (exec-raw (str "CREATE TRIGGER update_update_at_modtime BEFORE UPDATE "
                   "ON favors FOR EACH ROW EXECUTE PROCEDURE "
                   "update_created_at_column();"))

    (exec-raw (str "CREATE TRIGGER update_update_at_modtime BEFORE UPDATE "
                   "ON favor_list_items FOR EACH ROW EXECUTE PROCEDURE "
                   "update_created_at_column();"))

    (exec-raw (str "CREATE TRIGGER update_update_at_modtime BEFORE UPDATE "
                   "ON auctions FOR EACH ROW EXECUTE PROCEDURE "
                   "update_created_at_column();"))

   ))

(defn down []
  (transaction
   (exec-raw "DROP TABLE IF EXISTS users CASCADE")
   (exec-raw "DROP TABLE IF EXISTS favors CASCADE")
   (exec-raw "DROP TABLE IF EXISTS favor_list_items CASCADE")
   (exec-raw "DROP TABLE IF EXISTS auctions CASCADE")
   (exec-raw "DROP TABLE IF EXISTS authentications CASCADE")
))