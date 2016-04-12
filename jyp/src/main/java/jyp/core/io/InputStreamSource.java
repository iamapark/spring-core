package jyp.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public interface InputStreamSource {

    InputStream getInputStream() throws IOException;
}
