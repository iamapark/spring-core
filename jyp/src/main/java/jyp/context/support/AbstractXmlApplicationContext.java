package jyp.context.support;

import jyp.beans.factory.support.DefaultListableBeanFactory;
import jyp.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public abstract class AbstractXmlApplicationContext extends AbstractApplicationContext {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    protected void refreshBeanFactory() {

    }

    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(getParent());
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }
}
