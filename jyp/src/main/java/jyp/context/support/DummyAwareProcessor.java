package jyp.context.support;

import jyp.beans.factory.DummyAware;
import jyp.beans.factory.config.BeanPostProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public class DummyAwareProcessor implements BeanPostProcessor {

    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public Object postProcessorBeforeInitialization(Object bean, String name) {

        if (bean instanceof DummyAware) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invoking setDummy() on DummyAware bean '" + name + "'");
            }
            ((DummyAware)bean).setDummy(name + ".dumdum");
        }
        return bean;
    }

    @Override
    public Object postProcessorAfterInitialization(Object bean, String name) {
        return bean;
    }
}
