package jyp.context.support;

import jyp.beans.factory.config.BeanPostProcessor;
import jyp.context.ApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 12.
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    protected final Log logger = LogFactory.getLog(getClass());

    private final ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessorBeforeInitialization(Object bean, String name) {
        return bean;
    }

    @Override
    public Object postProcessorAfterInitialization(Object bean, String name) {
        return bean;
    }
}
