package jyp.beans.factory.support;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 20.
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    private BeanDefinitionRegistry beanFactory;

    private ClassLoader beanClassLoader = Thread.currentThread().getContextClassLoader();

    protected AbstractBeanDefinitionReader(BeanDefinitionRegistry beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanDefinitionRegistry getBeanFactory() {
        return beanFactory;
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }
}
