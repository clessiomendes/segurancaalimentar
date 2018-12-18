grails.gorm.default.constraints = {
    '*'(nullable: true)
}

grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

dataSource {
    dialect = "org.hibernate.dialect.PostgreSQLDialect"
    driverClassName = "org.postgresql.Driver"

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
            dbCreate = "update"
            url = "jdbc:postgresql://localhost:5432/sa"
            username = "postgres"
            password = "senha"
        }
    }
    validacao {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql://${System.getenv('POSTGRESQL_ADDON_HOST')}:${System.getenv('POSTGRESQL_ADDON_PORT')}/${System.getenv('POSTGRESQL_ADDON_DB')}"
            username = System.getenv('POSTGRESQL_ADDON_HOST')
            password = System.getenv('POSTGRESQL_ADDON_HOST')
        }
    }
    production {
        dataSource {
            dbCreate = "validate"
            url = "jdbc:postgresql://${System.getenv('POSTGRESQL_ADDON_HOST')}:${System.getenv('POSTGRESQL_ADDON_PORT')}/${System.getenv('POSTGRESQL_ADDON_DB')}"
            username = System.getenv('POSTGRESQL_ADDON_HOST')
            password = System.getenv('POSTGRESQL_ADDON_HOST')
        }
    }
}
