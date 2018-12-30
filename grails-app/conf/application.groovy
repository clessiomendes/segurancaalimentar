grails.gorm.default.constraints = {
    '*'(nullable: true)
}

grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

String aHost = System.getenv('POSTGRESQL_ADDON_HOST') ?: "localhost";
String aPort = System.getenv('POSTGRESQL_ADDON_PORT') ?: "5432";
String aDb = System.getenv('POSTGRESQL_ADDON_DB') ?: "sa";
String aUsername = System.getenv('POSTGRESQL_ADDON_USER') ?: "postgres";
String aPassword = System.getenv('POSTGRESQL_ADDON_PASSWORD') ?: "senha";

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
//            dbCreate = "update"     :mysql

            url = "jdbc:p6spy:postgresql://${aHost}:${aPort}/${aDb}"
            driverClassName = "com.p6spy.engine.spy.P6SpyDriver";

//            username = "postgres"
//            password = "senha"
        }
    }
    validacao {
        dataSource {
//            dbCreate = "update"
//            url = "jdbc:postgresql://${}:${System.getenv('')}/${System.getenv('')}"
//            username = System.getenv('')
//            password = System.getenv('')
        }
    }
    production {
        dataSource {
            dbCreate = "validate"
        }
    }
}

