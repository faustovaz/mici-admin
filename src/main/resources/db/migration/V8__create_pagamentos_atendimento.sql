create table pagamentos_atendimento(
  id integer primary key,
  id_atendimento integer,
  id_forma_pagamento integer,
  valor_pagamento real,
  dia_do_pagamento text
);