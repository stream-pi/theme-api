module com.StreamPi.ThemeAPI {
    requires transitive org.apache.logging.log4j;
    requires transitive java.xml;

    requires transitive com.StreamPi.Util;
    exports com.StreamPi.ThemeAPI;
}