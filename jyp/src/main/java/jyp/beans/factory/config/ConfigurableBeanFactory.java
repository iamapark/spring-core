package jyp.beans.factory.config;

import jyp.BeanFactory;
import jyp.beans.factory.HierarchicalBeanFactory;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory {

    void setParentBeanFactory(BeanFactory beanFactory);

    void ignoreDependencyType(Class type);

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    BeanDefinition getBeanDefinition(String beanName);

    void registerAlias(String beanName, String alias);

    void registerSingleton(String beanName, Object singletonObject);

    void destroySingletons();
}
