import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

import java.nio.charset.Charset

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        pattern =
//                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
//                        '%clr(%5p) ' + // Log level
//                        '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
//                        '%clr(%-40.40logger{39}){cyan} %clr(:){faint} ' + // Logger
//                        '%m%n%wex' // Message
//        "%d{ddMM HH:mm:ss} %-5level [%thread]: %message%n"
        "%d{ddMM HH:mm:ss} %-5level  > %message%n"
    }
}

def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir != null) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
}

logger 'segurancaalimentar.Application', DEBUG, ['STDOUT'], false

if (Environment.isDevelopmentMode()) {
    logger 'org.hibernate.type.descriptor.sql.BasicBinder', TRACE
    logger 'org.hibernate.SQL', TRACE
    logger 'segurancaalimentar', DEBUG
    logger 'org.apoiasuas', DEBUG
//            'grails.app.controllers',
//            'grails.app.services',
//            'grails.app.domain',
//            'grails.app.conf',
//            'grails.app.filters',

    logger 'grails.app.controllers', INFO
//    logger 'grails.app.controllers.mrhaki.grails.SampleController', DEBUG, ['STDOUT']
    logger 'grails.app.services', INFO
    logger 'grails.app.domain', INFO
    logger 'grails.app.conf', INFO

//    logger 'grails.app.controllers.*', DEBUG, ['STDOUT'], false
//    logger 'grails.app.services.*', DEBUG, ['STDOUT'], false
//    logger 'grails.app.domain.*', DEBUG, ['STDOUT'], false
//    logger 'grails.app.conf.*', DEBUG, ['STDOUT'], false
//    logger 'grails.app.filters.*', DEBUG, ['STDOUT'], false

//    root(DEBUG, ['STDOUT'])
}
//    logger 'org.apoiasuas', INFO

root(ERROR, ['STDOUT'])
