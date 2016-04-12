package jyp.context.support;

import jyp.beans.factory.config.ConfigurableListableBeanFactory;
import jyp.context.ApplicationContext;
import jyp.context.ConfigurableApplicationContext;
import jyp.core.io.DefaultResourceLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {

    /** Log4j logger used by this class. Available to subclasses. */
    protected final Log logger = LogFactory.getLog(getClass());
    /** BeanFactoryPostProcessors to apply on refresh */
    private final List beanFactoryPostProcessors = new ArrayList();
    /** Parent context */
    private ApplicationContext parent;
    /** Display name */
    private String displayName = getClass().getName() + ";hashCode=" + hashCode();

    /** System time in milliseconds when this context started */
    private long startupTime;

    /**
     * Load or reload configuration.
     * @throws org.springframework.context.ApplicationContextException if the configuration
     * was invalid or couldn't be found, or if configuration has already been loaded and
     * reloading is forbidden
     * @throws BeansException if the bean factory could not be initialized
     */
    @Override
    public void refresh() {
        this.startupTime = System.currentTimeMillis();

        // tell subclass to refresh the internal bean factory
        refreshBeanFactory();
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        //configure the bean factory with context semantics
        beanFactory.addBeanPostProcessor(new DummyAwareProcessor());
    }

    @Override
    public ApplicationContext getParent() {
        return this.parent;
    }

    protected abstract void refreshBeanFactory();

    public abstract ConfigurableListableBeanFactory getBeanFactory();
}
