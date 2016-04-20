package jyp.beans.factory.support;

import jyp.beans.PropertyValues;
import jyp.beans.factory.config.BeanDefinition;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 6.
 */
public abstract class AbstractBeanDefinition implements BeanDefinition {

    private PropertyValues propertyValues;

    private boolean singleton = true;

    private boolean lazyInit = false;

    public AbstractBeanDefinition(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    public boolean isSingleton() {
        return this.singleton;
    }

    @Override
    public PropertyValues getPropertyValues() {
        return this.propertyValues;
    }
}
