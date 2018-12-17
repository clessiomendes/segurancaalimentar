grails.gorm.default.constraints = {
    '*'(nullable: true)
}

grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

/*
dataSource {
    dbCreate = "update"
//    url = "jdbc:h2:file:C:/temp/db-segurancaalimentar;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
    url = "jdbc:h2:file:~/db-segurancaalimentar"
    username = "sa"
    password = ''
    driverClassName = "org.h2.Driver"
}
*/

dataSource {
    dialect = "org.hibernate.dialect.PostgreSQLDialect"
    driverClassName = "org.postgresql.Driver"
    dbCreate = "update"

    minEvictableIdleTimeMillis = 1800000
    timeBetweenEvictionRunsMillis = 1800000
    numTestsPerEvictionRun = 3
    testOnBorrow = true
    testOnConnect = true
    testWhileIdle = true
    testOnReturn = true
    validationQuery = "SELECT 1*1"
    maxActive = 3
    initialSize = 1
    minIdle = 1
    maxIdle = 1
}

environments {
    development {
        dataSource {
            url = "jdbc:postgresql://localhost:5432/sa"
            username = "postgres"
            password = "senha"
        }
    }
    production {
        dataSource {
            url = "${System.getProperties().getProperty("POSTGRESQL_ADDON_URI")}"
        }
    }
}
