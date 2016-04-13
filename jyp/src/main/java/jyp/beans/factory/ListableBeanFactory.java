package jyp.beans.factory;

public interface ListableBeanFactory extends BeanFactory {
    String[] getBeanDefinitionNames();

    int getBeanDefinitionCount();
}