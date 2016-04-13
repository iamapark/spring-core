package jyp.beans.factory;

import jyp.beans.factory.BeanFactory;

public interface ListableBeanFactory extends BeanFactory {
    String[] getBeanDefinitionNames();

    int getBeanDefinitionCount();
}