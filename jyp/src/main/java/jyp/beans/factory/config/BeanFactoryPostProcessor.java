package jyp.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 13.
 */
public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
