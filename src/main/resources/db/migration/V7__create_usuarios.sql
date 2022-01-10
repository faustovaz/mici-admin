create table usuarios(
	username text,
	password text
);

-- To generate bcrypt passwords on shel: $ htpasswd -nbBC 4 USER PASSWORD
insert into usuarios(username, password) values ('admin', '$2y$04$Ne4z5JpcKTwx8JTi.ZaZCesNoNYb4KshgagfMU74LREZIennHmt1e');
