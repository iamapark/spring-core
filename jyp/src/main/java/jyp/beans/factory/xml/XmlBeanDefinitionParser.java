package jyp.beans.factory.xml;

import jyp.beans.factory.support.BeanDefinitionRegistry;
import jyp.core.io.Resource;
import org.springframework.beans.BeansException;
import org.w3c.dom.Document;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 20.
 */
public interface XmlBeanDefinitionParser {

    void registerBeanDefinitions(BeanDefinitionRegistry beanFactory,
                                 ClassLoader beanClassLoader,
                                 Document document,
                                 Resource resource) throws BeansException;
}
