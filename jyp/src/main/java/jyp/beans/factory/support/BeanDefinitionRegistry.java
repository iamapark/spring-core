package jyp.beans.factory.support;

import jyp.beans.factory.config.BeanDefinition;

public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String id, BeanDefinition beanDefinition);
}
