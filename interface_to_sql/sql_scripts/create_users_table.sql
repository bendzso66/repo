CREATE table users (
	user_id INTEGER NOT NULL AUTO_INCREMENT,	# egyedi azonosító
	email VARCHAR(50) NOT NULL UNIQUE,			# email cím amely egyben a felhasználónév is (szeretnénk ezt külön letárolni?)
	password VARCHAR(50) NOT NULL,				# egylőre titkosítatlanul, ha belép, akkor bejelentkezésenként egyedi azonosítót generálok, ami majd a http kérésekben azonosítja (ezt is érdemes itt letárolni?)
	search_range REAL,							# a maximum megengedett távolság, amin belül parkolóhely érdekli a felhasználót
	last_login LONG,							# utolsó belépés, pl.: fontos lehet a munkamenet lejératának megállapításához, ha lejár, újra be kell jelentkeznie
	recommended_lots INTEGER,					# felhasználó által beküldött szabad helyek száma, statisztikához
	lot_requests INTEGER,						# parkolóhely lekérdezések száma, statisztikához
	PRIMARY KEY(user_id)
);