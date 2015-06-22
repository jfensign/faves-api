(ns migrations.001-base-schema
  (:use [korma core db]))

(defn up []
  (transaction

   (exec-raw (str "CREATE OR REPLACE FUNCTION update_created_at_column() "
                  "RETURNS TRIGGER AS ' "
                  "BEGIN "
                    "NEW.updated_at = NOW(); "
                    "RETURN NEW; "
                  "END; "
                  "' LANGUAGE 'plpgsql';"))

   (exec-raw (str "CREATE TABLE users ("
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
                  "role character varying(255) default 'none',"
                  "gender character varying(255) default '',"
                  "birthdate date,"
                  "unlock_token character varying(255),"
                  "locked_at timestamp without time zone,"
                  "verified_status integer NOT NULL DEFAULT 0"
                  ")"))


   (exec-raw (str "CREATE TABLE authentications ("
                  "id serial PRIMARY KEY,"
                  "user_id integer,"
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

   (exec-raw (str "CREATE TABLE favors ("
                  "id SERIAL PRIMARY KEY,"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "title TEXT NOT NULL,"
                  "instructions TEXT NOT NULL,"
                  "user_id INT REFERENCES users(id)"
                  ")"))

   (exec-raw (str "CREATE TABLE favor_list_items ("
                  "id SERIAL PRIMARY KEY,"
                  "favor_id INT REFERENCES favors(id),"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "title TEXT NOT NULL,"
                  "instructions TEXT NOT NULL,"
                  "user_id INT REFERENCES users(id)"
                  ")"))

   (exec-raw (str "CREATE TABLE auctions ("
                  "id SERIAL PRIMARY KEY,"
                  "updated_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                  "favor_id INT REFERENCES favors(id)"
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

    (exec-raw (str "CREATE INDEX index_authentications_on_user_id"
                    " ON authentications"
                    " USING btree"
                    " (user_id)"))

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