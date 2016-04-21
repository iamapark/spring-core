package jyp.context;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    void setParent(ApplicationContext parent);

    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor);

    void refresh();

}
