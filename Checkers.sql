CREATE TABLE "users" (
  "user_id" SERIAL PRIMARY KEY,
  "username" varchar,
  "first_name" varchar,
  "last_name" varchar,
  "password" varchar,
  "date_of_birth" date,
  "email_address" varchar,
  "logged_in" boolean
);

CREATE TABLE "games" (
  "game_id" SERIAL PRIMARY KEY,
  "player1" varchar,
  "player2" varchar,
  "startTime" timestamp,
  "endTime" timestamp,
  "winner" varchar
);

CREATE TABLE "user_games" (
  "user_id" int,
  "game_id" int,
  PRIMARY KEY ("user_id", "game_id")
);

ALTER TABLE "user_games" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "user_games" ADD FOREIGN KEY ("game_id") REFERENCES "games" ("game_id");

