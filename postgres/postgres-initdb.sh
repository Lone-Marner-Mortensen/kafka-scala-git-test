psql --variable=ON_ERROR_STOP=1 --username "postgres" <<-EOSQL
    CREATE DATABASE demo;
    GRANT ALL PRIVILEGES ON DATABASE "demo" TO postgres;
EOSQL