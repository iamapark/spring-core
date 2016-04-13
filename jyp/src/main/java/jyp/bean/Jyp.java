package jyp.bean;

import jyp.beans.factory.BeanFactory;
import jyp.beans.factory.BeanFactoryAware;
import jyp.beans.factory.BeanNameAware;
import jyp.beans.factory.DummyAware;

/**
 * @author jinyoung.park89
 * @date 2016. 3. 30.
 */
public class Jyp implements SpringCoreMember, BeanNameAware, BeanFactoryAware, DummyAware {

    private String name = "박진영";

    private String beanName;
    private BeanFactory beanFactory;
    private String dummy;

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
}
