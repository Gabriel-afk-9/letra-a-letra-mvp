CREATE TABLE "avatar" (
                          "avatar_id" varchar(50) PRIMARY KEY NOT NULL
);

CREATE TABLE "user" (
                        "user_id" uuid PRIMARY KEY NOT NULL,
                        "username" varchar(15) UNIQUE NOT NULL,
                        "email" varchar(50) UNIQUE NOT NULL,
                        "password_hash" varchar(100),
                        "google_id" varchar(100) UNIQUE,
                        "avatar_id" varchar(50) DEFAULT 'old-man' REFERENCES "avatar" ("avatar_id"),
                        "can_change_nickname" boolean DEFAULT TRUE,
                        "created_at" timestamptz DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT check_auth_method
                            CHECK (password_hash IS NOT NULL OR google_id IS NOT NULL)
);

CREATE TABLE "user_stats" (
                        "user_id" uuid PRIMARY KEY REFERENCES "user" ("user_id") ON DELETE CASCADE,
                        "total_matches" integer DEFAULT 0,
                        "total_wins" integer DEFAULT 0,
                        "win_streak" integer DEFAULT 0,
                        "points" integer DEFAULT 0
);

CREATE TABLE "game" (
                        "game_id" uuid PRIMARY KEY NOT NULL,
                        "host_id" uuid REFERENCES "user" ("user_id"),
                        "created_by_id" uuid REFERENCES "user" ("user_id"),
                        "room_code" varchar(50) NOT NULL,
                        "game_type" varchar(50) NOT NULL,
                        "created_at" timestamp DEFAULT CURRENT_TIMESTAMP,
                        "status" varchar(50)
);

CREATE TABLE "matches" (
                        "match_id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                        "game_id" uuid REFERENCES "game" ("game_id") ON DELETE CASCADE,
                        "game_mode" varchar(50) NOT NULL,
                        "started_at" timestamp DEFAULT CURRENT_TIMESTAMP,
                        "ended_at" timestamp
);

CREATE TABLE "match_players" (
                        "match_id" uuid REFERENCES "matches" ("match_id") ON DELETE CASCADE,
                        "user_id" uuid REFERENCES "user" ("user_id") ON DELETE CASCADE,
                        "score" integer DEFAULT 0,
                        "is_winner" boolean DEFAULT false,
                        PRIMARY KEY ("match_id", "user_id")
);

CREATE TABLE "user_avatars" (
                        "user_id" uuid REFERENCES "user" ("user_id") ON DELETE CASCADE,
                        "avatar_id" varchar(50) REFERENCES "avatar" ("avatar_id") ON DELETE CASCADE,
                        "unlocked_at" timestamptz DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY ("user_id", "avatar_id")
);

CREATE INDEX idx_game_room_code_active ON "game" ("room_code") WHERE status = 'WAITING';
CREATE INDEX idx_user_stats_wins ON "user_stats" ("total_wins" DESC);
CREATE INDEX idx_match_players_user ON "match_players" ("user_id");