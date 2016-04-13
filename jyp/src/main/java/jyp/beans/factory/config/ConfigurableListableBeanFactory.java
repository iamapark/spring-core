package jyp.beans.factory.config;

import jyp.beans.factory.ListableBeanFactory;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, ConfigurableBeanFactory {

    void preInstantiateSingletons();
}
