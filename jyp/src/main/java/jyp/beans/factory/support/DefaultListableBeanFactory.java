package jyp.beans.factory.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jyp.beans.factory.BeanFactory;
import jyp.beans.factory.config.BeanDefinition;
import jyp.beans.factory.config.ConfigurableListableBeanFactory;
import jyp.context.support.DummyAwareProcessor;

public class DefaultListableBeanFactory extends AbstractBeanFactory
        implements BeanDefinitionRegistry, ConfigurableListableBeanFactory {

    private Map<String, BeanDefinition> beanDefinitionHash = new HashMap<>();

    public DefaultListableBeanFactory(BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        Set keys = beanDefinitionHash.keySet();
        String[] names = new String[keys.size()];
        Iterator itr = keys.iterator();
        int i = 0;
        while (itr.hasNext()) {
            names[i++] = (String)itr.next();
        }
        return names;
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitionHash.size();
    }

    protected void preInstantiate() {

        super.addBeanPostProcessor(new DummyAwareProcessor());

        String[] beanNames = getBeanDefinitionNames();
        for (int i = 0; i < getBeanDefinitionCount(); i++) {
            getBean(beanNames[i]);
        }
    }

    @Override
    public void registerBeanDefinition(String id, BeanDefinition beanDefinition) {
        beanDefinitionHash.put(id, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String key) {
        return beanDefinitionHash.get(key);
    }


    public void clear() {
        super.clear();
        this.beanDefinitionHash.clear();
    }

    //---------------------------------------------------------------------
    // Implementation of ConfigurableListableBeanFactory
    //---------------------------------------------------------------------
    @Override
    public void preInstantiateSingletons() {
        // Todo: 구현 필요!!
    }
}
