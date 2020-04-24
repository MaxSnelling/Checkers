CREATE TABLE "users" (
  "user_id" SERIAL PRIMARY KEY,
  "username" varchar,
  "first_name" varchar,
  "last_name" varchar,
  "password" varchar,
  "date_of_birth" date,
  "email_address" varchar
);

CREATE TABLE "games" (
  "game_id" SERIAL PRIMARY KEY,
  "player1" varchar,
  "player2" varchar,
  "startTime" timestamp,
  "endTime" timestamp,
  "winner" varchar,
  "admin_id" int
);

CREATE TABLE "user_games" (
  "game_id" int,
  "user_id" int,
  PRIMARY KEY ("game_id", "user_id")
);

ALTER TABLE "user_games" ADD FOREIGN KEY ("game_id") REFERENCES "users" ("user_id");

ALTER TABLE "user_games" ADD FOREIGN KEY ("user_id") REFERENCES "games" ("game_id");

