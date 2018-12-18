rem exportando bd local
C:/postgres9/bin\pg_dump.exe --host localhost --port 5432 --username "postgres" --no-password  --format custom --blobs --no-privileges --no-tablespaces --verbose --no-unlogged-table-data --file "C:\Dropbox\develop\segalim\data\segalim.backup" "sa"

rem importando bd remoto
C:/postgres9/bin\pg_restore.exe --host birhqh6cukaewdch9nze-postgresql.services.clever-cloud.com --port 5432 --username "u6d92kuqbd4sktvhdqyc" --dbname "birhqh6cukaewdch9nze" --no-password  --no-owner --no-privileges --no-tablespaces --clean --verbose "C:\Dropbox\develop\segalim\data\segalim.backup"