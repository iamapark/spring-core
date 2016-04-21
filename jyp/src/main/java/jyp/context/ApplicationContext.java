package jyp.context;

import jyp.ListableBeanFactory;
import jyp.beans.factory.HierarchicalBeanFactory;
import jyp.core.io.ResourceLoader;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public interface ApplicationContext extends ListableBeanFactory, HierarchicalBeanFactory, ResourceLoader {

    ApplicationContext getParent();

    String getDisplayName();

    /**
     * Return the timestamp when this context was first loaded.
     * @return the timestamp (ms) when this context was first loaded
     */
    long getStartupDate();
}
