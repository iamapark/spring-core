package jyp.context.support;

import jyp.beans.factory.BeanFactory;
import jyp.beans.factory.config.ConfigurableListableBeanFactory;
import jyp.context.ApplicationContext;
import jyp.context.ConfigurableApplicationContext;
import jyp.core.io.DefaultResourceLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

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
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    /** Parent context */
    private ApplicationContext parent;
    /** Display name */
    private String displayName = getClass().getName() + ";hashCode=" + hashCode();

    /** System time in milliseconds when this context started */
    private long startupTime;

    public AbstractApplicationContext() {
    }

    public AbstractApplicationContext(ApplicationContext parent) {
        this.parent = parent;
    }

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

    @Override
    public void setParent(ApplicationContext parent) {
        this.parent = parent;
    }

    protected abstract void refreshBeanFactory();

    public abstract ConfigurableListableBeanFactory getBeanFactory();

    /**
     * ApplicationContext가 BeanFactory를 상속한 이유: ApplicationContext를 proxy 처럼 사용하기 위함
     */
    @Override
    public Object getBean(String name) {
        return getBeanFactory().getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        return getBeanFactory().getBean(name, clazz);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public int getBeanDefinitionCount() {
        return getBeanFactory().getBeanDefinitionCount();
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor) {
        this.beanFactoryPostProcessors.add(beanFactoryPostProcessor);
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public long getStartupDate() {
        return this.startupTime;
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return getParent();
    }
}
