create table formas_pagamento(
	id integer primary key,
	nome text	
);

insert into formas_pagamento(nome) values ('Cartão');
insert into formas_pagamento(nome) values ('Cortesia');
insert into formas_pagamento(nome) values ('Dinheiro');
insert into formas_pagamento(nome) values ('Pix');
