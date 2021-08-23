create table atendimentos(
	id integer primary key,
	pagamento_realizado integer,
	dia_do_atendimento text,
	observacao text,
	valor_atendimento real,
	id_forma_pagamento integer,
	id_cliente integer,
	cortesia integer,
	ultima_atualizacao text
);

