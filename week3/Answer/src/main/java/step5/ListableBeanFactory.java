package step5;

public interface ListableBeanFactory extends BeanFactory {
    String[] getBeanDefinitionNames();
    int getBeanDefinitionCount();
}