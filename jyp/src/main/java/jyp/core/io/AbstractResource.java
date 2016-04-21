package jyp.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public abstract class AbstractResource implements Resource {

    protected static final String URL_PROTOCOL_FILE = "file";

    @Override
    public boolean exists() {
        try {
            return getFile().exists();
        } catch (IOException ex) {
            try {
                InputStream is = getInputStream();
                is.close();
                return true;
            } catch (IOException ex2) {
                return false;
            }
        }
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    @Override
    public File getFile() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
    }

    public String toString() {
        return getDescription();
    }

    /**
     * This implementation compares description strings.
     * @see #getDescription
     */
    public boolean equals(Object obj) {
        return (obj instanceof Resource && ((Resource)obj).getDescription().equals(getDescription()));
    }

    /**
     * This implementation returns the description's hash code.
     * @see #getDescription
     */
    public int hashCode() {
        return getDescription().hashCode();
    }
}
