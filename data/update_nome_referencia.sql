update familia set nome_referencia = subquery.nome, nis_referencia = subquery.nis
from (select familia_id, nome, nis from cidadao where parentesco = 'Pessoa Responsavel pela Unidade Familiar') as subquery
where familia.id = subquery.familia_id;
