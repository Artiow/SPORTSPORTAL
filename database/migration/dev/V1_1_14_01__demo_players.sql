insert into tournament.player (id, name, surname, birthdate)
values (1, 'игрок1имя', 'игрок1фамилия', '2001-01-01 00:00:00'),
       (2, 'игрок2имя', 'игрок2фамилия', '2001-01-01 00:00:00'),
       (3, 'игрок3имя', 'игрок3фамилия', '2001-01-01 00:00:00'),
       (4, 'игрок4имя', 'игрок4фамилия', '2001-01-01 00:00:00'),
       (5, 'игрок5имя', 'игрок5фамилия', '2001-01-01 00:00:00'),
       (6, 'игрок6имя', 'игрок6фамилия', '2001-01-01 00:00:00'),
       (7, 'игрок7имя', 'игрок7фамилия', '2001-01-01 00:00:00'),
       (8, 'игрок8имя', 'игрок8фамилия', '2001-01-01 00:00:00'),
       (9, 'игрок9имя', 'игрок9фамилия', '2001-01-01 00:00:00'),
       (10, 'игрок10имя', 'игрок10фамилия', '2001-01-01 00:00:00'),
       (11, 'игрок11имя', 'игрок11фамилия', '2001-01-01 00:00:00'),
       (12, 'игрок12имя', 'игрок12фамилия', '2001-01-01 00:00:00'),
       (13, 'игрок13имя', 'игрок13фамилия', '2001-01-01 00:00:00'),
       (14, 'игрок14имя', 'игрок14фамилия', '2001-01-01 00:00:00'),
       (15, 'игрок15имя', 'игрок15фамилия', '2001-01-01 00:00:00'),
       (16, 'игрок16имя', 'игрок16фамилия', '2001-01-01 00:00:00'),
       (17, 'игрок17имя', 'игрок17фамилия', '2001-01-01 00:00:00'),
       (18, 'игрок18имя', 'игрок18фамилия', '2001-01-01 00:00:00'),
       (19, 'игрок19имя', 'игрок19фамилия', '2001-01-01 00:00:00'),
       (20, 'игрок10имя', 'игрок20фамилия', '2001-01-01 00:00:00'),
       (21, 'игрок21имя', 'игрок21фамилия', '2001-01-01 00:00:00'),
       (22, 'игрок22имя', 'игрок22фамилия', '2001-01-01 00:00:00'),
       (23, 'игрок23имя', 'игрок23фамилия', '2001-01-01 00:00:00'),
       (24, 'игрок24имя', 'игрок24фамилия', '2001-01-01 00:00:00'),
       (25, 'игрок25имя', 'игрок25фамилия', '2001-01-01 00:00:00'),
       (26, 'игрок26имя', 'игрок26фамилия', '2001-01-01 00:00:00'),
       (27, 'игрок27имя', 'игрок27фамилия', '2001-01-01 00:00:00'),
       (28, 'игрок28имя', 'игрок28фамилия', '2001-01-01 00:00:00'),
       (29, 'игрок29имя', 'игрок29фамилия', '2001-01-01 00:00:00'),
       (30, 'игрок30имя', 'игрок30фамилия', '2001-01-01 00:00:00');

insert into common."user" (id, password, name, surname, email, disabled)
values (3,
        '$2y$10$vPdUPxG1tl6PZyo7rLjmdOUlF42efYoepL.C/hej.8twN6mjO6iJS',
        'игрок10имя',
        'игрок10фамилия',
        'player10@mail.com',
        false),
       (4,
        '$2y$10$vPdUPxG1tl6PZyo7rLjmdOUlF42efYoepL.C/hej.8twN6mjO6iJS',
        'игрок20имя',
        'игрок20фамилия',
        'player20@mail.com',
        false),
       (5,
        '$2y$10$vPdUPxG1tl6PZyo7rLjmdOUlF42efYoepL.C/hej.8twN6mjO6iJS',
        'игрок30имя',
        'игрок30фамилия',
        'player30@mail.com',
        false);

insert into tournament.player_binding (user_id, player_id)
values (3, 10),
       (4, 20),
       (5, 30);