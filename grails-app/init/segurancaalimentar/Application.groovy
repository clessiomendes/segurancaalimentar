package segurancaalimentar

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        System.out.println("Application main System.properties['APP_HOME'] "+System.getProperty("APP_HOME"));
        System.out.println("Application main System.getenv('APP_HOME'] "+System.getenv("APP_HOME"));
        System.out.println("Application main System.getProperty['APP_HOME'] "+System.properties["APP_HOME"]);
//        System.out.println("Application main System.getProperty('JAVA_VERSION') "+System.getProperty("JAVA_VERSION"));
//        System.out.println("Application main System.getProperty('JAVA_VERSION') "+System.properties["JAVA_VERSION"]);
        GrailsApp.run(Application, args)
    }
}