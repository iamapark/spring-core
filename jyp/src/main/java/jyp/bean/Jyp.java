package jyp.bean;

import jyp.beans.factory.*;

/**
 * @author jinyoung.park89
 * @date 2016. 3. 30.
 */
public class Jyp implements SpringCoreMember, BeanNameAware, BeanFactoryAware, DummyAware, InitializingBean,
        DisposableBean {

    private String name = "박진영";

    private String beanName;
    private BeanFactory beanFactory;
    private String dummy;
    private Boolean isInitialized;

    @Override
    public String getName() {
        return this.name;
    }

    public String getBeanName() {
        return this.beanName;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public Boolean getInitialized() {
        return isInitialized;
    }

    public String getDummy() {
        return dummy;
    }

    @Override
    public void setDummy(String dummy) {
        this.dummy = dummy;
    }

    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() {
        isInitialized = true;
    }

    @Override
    public void destroy() throws Exception {

    }
}
