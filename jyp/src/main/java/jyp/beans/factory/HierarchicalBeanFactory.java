package jyp.beans.factory;

import jyp.BeanFactory;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public interface HierarchicalBeanFactory extends BeanFactory {

    BeanFactory getParentBeanFactory();
}
