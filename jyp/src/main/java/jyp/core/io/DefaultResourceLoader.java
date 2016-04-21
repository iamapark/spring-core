package jyp.core.io;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public class DefaultResourceLoader implements ResourceLoader {

    @Override
    public Resource getResource(String location) {
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()));
        } else {
            try {
                URL url = new URL(location);
                return new UrlResource(url);
            } catch (MalformedURLException e) {
                return new ClassPathResource(location);
            }

        }
    }
}
