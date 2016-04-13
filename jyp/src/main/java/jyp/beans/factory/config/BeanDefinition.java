package jyp.beans.factory.config;

import jyp.beans.PropertyValues;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 6.
 */
public interface BeanDefinition {

    PropertyValues getPropertyValues();

    ConstructorArgumentValues getConstructorArgumentValues();
}
