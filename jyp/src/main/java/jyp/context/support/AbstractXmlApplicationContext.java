package jyp.context.support;

import jyp.beans.factory.support.DefaultListableBeanFactory;
import jyp.beans.factory.config.ConfigurableListableBeanFactory;
import jyp.beans.factory.xml.XmlBeanDefinitionReader;
import jyp.context.ApplicationContext;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public abstract class AbstractXmlApplicationContext extends AbstractApplicationContext {

    private ConfigurableListableBeanFactory beanFactory;

    public AbstractXmlApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    public AbstractXmlApplicationContext() {
    }

    @Override
    protected void refreshBeanFactory() {
        DefaultListableBeanFactory beanFactory = createBeanFactory();

        XmlBeanDefinitionReader definitionReader = new XmlBeanDefinitionReader(beanFactory);
        loadBeanDefinitions(definitionReader);

        this.beanFactory = beanFactory;
    }

    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(getParent());
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public void loadBeanDefinitions(XmlBeanDefinitionReader reader) {
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            for (String configLocation : configLocations) {
                reader.loadBeanDefinitions(getResource(configLocation));
            }
        }
    }

    protected abstract String[] getConfigLocations();
}
