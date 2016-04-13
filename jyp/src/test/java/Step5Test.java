import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import jyp.beans.factory.xml.XmlBeanFactory;
import jyp.bean.Jyp;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jinyoung.park89
 * @date 2016. 3. 31.
 */
public class Step5Test {

    //Todo: Bean Life Cycle 구현

    /**
     * spring-framework-1.0
     *
     * Bean factories are supposed to support the standard bean lifecycle interfaces
     * as far as possible. The maximum set of initialization methods and their standard
     * order is:
     * 1. BeanNameAware's setBeanName
     * 2. BeanFactoryAware's setBeanFactory
     * 3. ApplicationContextAware's setApplicationContext (only applicable if running
     * in an application context)
     * 4. postProcessBeforeInitialization methods of BeanPostProcessors<br>
     * 5. InitializingBean's afterPropertiesSet
     * 6. a custom init-method definition
     * 7. postProcessAfterInitialization methods of BeanPostProcessors
     *
     * On shutdown of a bean factory, the following lifecycle methods apply:<br>
     * 1. DisposableBean's destroy
     * 2. a custom destroy-method definition
     */

    private XmlBeanFactory beanFactory;
    private ApplicationContext applicationContext;
    private Jyp jyp;

    @Before
    public void setup() {
        beanFactory = new XmlBeanFactory(ClassLoader.getSystemResourceAsStream("step5.xml"));
        jyp = beanFactory.getBean("jyp", Jyp.class);

        applicationContext = new ClassPathXmlApplicationContext("4.2.3.xml");
    }

    //1. BeanNameAware's setBeanName
    @Test
    public void test_BeanNameAware() {
        String beanName = jyp.getBeanName();
        assertEquals("jyp", beanName);
    }

    //2. BeanFactoryAware's setBeanFactory
    @Test
    public void test_BeanFactoryAware() {
        assertEquals(jyp.getBeanFactory(), beanFactory);
    }

    //3. ApplicationContextAware's setApplicationContext (only applicable if running in an application context)
    @Test
    public void test_ApplicationContextAware() {

    }

    //4. postProcessBeforeInitialization methods of BeanPostProcessors
    @Test
    public void test_PostProcessorBeforeInitialization() {
        String dummy = jyp.getDummy();
        assertEquals("jyp.dumdum", dummy);
    }
}
