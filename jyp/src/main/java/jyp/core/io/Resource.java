package jyp.core.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public interface Resource extends InputStreamSource {

    boolean exists();

    boolean isOpen();

    URL getURL() throws IOException;

    File getFile() throws IOException;

    String getDescription();
}
