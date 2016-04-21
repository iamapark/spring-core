package jyp.core.io;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = "classpath:";

    Resource getResource(String location);
}
