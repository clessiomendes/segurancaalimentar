grails.gorm.default.constraints = {
    '*'(nullable: true)
}

grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

String aHost = System.getenv('POSTGRESQL_ADDON_HOST') ?: "localhost";
String aPort = System.getenv('POSTGRESQL_ADDON_HOST') ?: "5432";
String aDb = System.getenv('POSTGRESQL_ADDON_HOST') ?: "sa";
String aUsername = System.getenv('POSTGRESQL_ADDON_HOST') ?: "postgres";
String aPassword = System.getenv('POSTGRESQL_ADDON_HOST') ?: "senha";

//Configuracoes gerais para todos os ambientes (sobrescrever configuracoes de ambientes especificos na sequencia)
dataSource {
    dialect = "org.hibernate.dialect.PostgreSQLDialect"
    driverClassName = "org.postgresql.Driver"

    url = "jdbc:postgresql://${aHost}:${aPort}/${aDb}"
    username = aUsername
    password = aPassword

    dbCreate = "update"

    minEvictableIdleTimeMillis = 1800000
    timeBetweenEvictionRunsMillis = 1800000
    numTestsPerEvictionRun = 1
    testOnBorrow = true
    testOnConnect = true
    testWhileIdle = true
    testOnReturn = true
    validationQuery = "SELECT 1*1"
    maxActive = 2
    initialSize = 1
    minIdle = 1
    maxIdle = 2

}

environments {
    development {
        dataSource {
//            dbCreate = "update"
//            url = "jdbc:postgresql://localhost:5432/sa"
//            username = "postgres"
//            password = "senha"
        }
    }
    validacao {
        dataSource {
//            dbCreate = "update"
//            url = "jdbc:postgresql://${}:${System.getenv('POSTGRESQL_ADDON_PORT')}/${System.getenv('POSTGRESQL_ADDON_DB')}"
//            username = System.getenv('POSTGRESQL_ADDON_USER')
//            password = System.getenv('POSTGRESQL_ADDON_PASSWORD')
        }
    }
    production {
        dataSource {
            dbCreate = "validate"
        }
    }
}
