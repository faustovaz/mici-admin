alter table atendimentos add column sera_cobrado integer;
alter table atendimentos add column is_cronograma integer;
alter table atendimentos rename column cortesia to corsesia_not_used;