package jyp.context;

import jyp.core.io.ResourceLoader;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 15.
 */
public interface ResourceLoaderAware {

    void setResourceLoader(ResourceLoader resourceLoader);
}
