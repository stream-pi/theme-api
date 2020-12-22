module com.StreamPi.ThemeAPI {
    requires transitive org.apache.logging.log4j;
    requires transitive org.apache.commons.configuration2;

    requires transitive com.StreamPi.Util;
    exports com.StreamPi.ThemeAPI;
}