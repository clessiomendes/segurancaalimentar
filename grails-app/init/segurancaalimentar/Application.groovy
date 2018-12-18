package segurancaalimentar

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        System.out.println("Application main System.properties['URL_DB'] "+System.properties["URL_DB"]);
        System.out.println("Application main System.getProperty('URL_DB') "+System.getProperty("URL_DB"));
        GrailsApp.run(Application, args)
    }
}