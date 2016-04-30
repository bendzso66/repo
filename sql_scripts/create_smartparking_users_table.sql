CREATE table smartparking_users (
    id INTEGER NOT NULL AUTO_INCREMENT,         # egyedi azonosító
    password VARCHAR(50) NOT NULL,              # egylőre titkosítatlanul, ha belép, akkor bejelentkezésenként egyedi azonosítót generálok, ami majd a http kérésekben azonosítja (ezt is érdemes itt letárolni?)
    search_range INTEGER DEFAULT 0.5,           # a maximum megengedett távolság méterben, amin belül parkolóhely érdekli a felhasználót
    lot_requests INTEGER DEFAULT 0,             # parkolóhely lekérdezések száma, statisztikához
    last_login BIGINT(20),                      # utolsó belépés, pl.: fontos lehet a munkamenet lejératának megállapításához, ha lejár, újra be kell jelentkeznie
    recommended_lots INTEGER DEFAULT 0,         # felhasználó által beküldött szabad helyek száma, statisztikához
    email VARCHAR(50) NOT NULL UNIQUE,          # email cím amely egyben a felhasználónév is (szeretnénk ezt külön letárolni?)
    time_of_submission BIGINT(20),
    PRIMARY KEY(id)
)
CHARACTER SET utf8 COLLATE utf8_unicode_ci;
