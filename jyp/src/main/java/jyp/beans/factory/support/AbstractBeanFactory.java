package jyp.beans.factory.support;

import jyp.*;
import jyp.beans.PropertyValue;
import jyp.beans.PropertyValues;
import jyp.beans.factory.*;
import jyp.beans.factory.config.BeanDefinition;
import jyp.beans.factory.config.BeanPostProcessor;
import jyp.beans.factory.config.ConstructorArgumentValues;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractBeanFactory implements BeanFactory {

    private static final Object CURRENTLY_IN_CREATION = new Object();
    protected final Log logger = LogFactory.getLog(getClass());
    private final BeanFactory parentBeanFactory;

    private final List beanPostProcessors = new ArrayList<>();

    /**
     * Map of Bean objects, keyed by id attribute
     */
    private Map<String, Object> beanHash = new HashMap<>();

    public AbstractBeanFactory(BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    public abstract BeanDefinition getBeanDefinition(String key);

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    public List getBeanPostProcessors() {
        return beanPostProcessors;
    }

    @Override
    public <T> T getBean(String key, Class<T> clazz) {
        return (T)getBean(key);
    }

    @Override
    public Object getBean(String key) {
        return getBeanInternal(key);
    }

    private Object getBeanInternal(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bean name null is not allowed");
        }

        Object sharedInstance = beanHash.get(key);
        if (sharedInstance != null) {
            if (sharedInstance == CURRENTLY_IN_CREATION) {
                throw new BeanCurrentlyInCreationException(
                    key + " Requested bean is already currently in creation");
            }
            if (logger.isDebugEnabled()) {
                logger.debug("returning cached object: " + key);
            }
            return sharedInstance;

        } else {
            BeanDefinition beanDefinition = getBeanDefinition(key);
            if (beanDefinition != null) {
                sharedInstance = beanHash.get(key);
                if (sharedInstance == null) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Creating shared instance of bean '" + key + "'");
                    }
                    this.beanHash.put(key, CURRENTLY_IN_CREATION);
                    try {
                        sharedInstance = createBean(key);
                        this.beanHash.put(key, sharedInstance);
                    } catch (BeanCurrentlyInCreationException e) {
                        this.beanHash.remove(key);
                        throw e;
                    }
                }

                return sharedInstance;
            } else {
                if (this.parentBeanFactory == null) {
                    throw new IllegalArgumentException(
                        "Cannot instantiate [bean name : " + key + "]; is not exist");
                }
                return parentBeanFactory.getBean(key);
            }
        }
    }

    private Object createBean(String key) {
        try {
            RootBeanDefinition rootBeanDefinition = (RootBeanDefinition)getBeanDefinition(key);
            PropertyValues propertyValues = rootBeanDefinition.getPropertyValues();

            Object bean;

            if (rootBeanDefinition.isCreateWithConstructor()) {

                ConstructorArgumentValues constructorArgumentValues = rootBeanDefinition.getConstructorArgumentValues();
                List<ConstructorArgument> constructorList = constructorArgumentValues.getConstructorArguments();
                Object[] refBeans = new Object[constructorList.size()];
                Class[] refBeanClass = new Class[constructorList.size()];

                for (int i = 0; i < constructorList.size(); i++) {
                    Object refBean = getBean(constructorList.get(i).getRefName());
                    refBeanClass[i] = refBean.getClass();
                    refBeans[i] = refBean;
                }

                Class beanClass = rootBeanDefinition.getBeanClass();
                Constructor constructor = beanClass.getConstructor(refBeanClass);
                bean = constructor.newInstance(refBeans);

            } else {
                bean = rootBeanDefinition.getBeanClass().newInstance();
            }

            applyPropertyValues(rootBeanDefinition, propertyValues, bean, key);
            bean = callLifecycleMethodsIfNecessary(bean, key);

            return bean;
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(
                "Cannot instantiate [bean name : " + key + "]; is it an interface or an abstract class?");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot instantiate [bean name : " + key
                + "]; has class definition changed? Is there a public constructor?");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot instantiate [bean name: " + key + "]");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot instantiate [bean name: " + key + "]");
        }
    }

    private void applyPropertyValues(RootBeanDefinition rootBeanDefinition,
                                     PropertyValues propertyValues,
                                     Object bean,
                                     String beanName) {
        Class clazz = rootBeanDefinition.getBeanClass();

        PropertyValue[] array = propertyValues.getPropertyValues();
        for (int i = 0; i < propertyValues.getCount(); ++i) {
            PropertyValue property = array[i];
            try {
                Field field = clazz.getDeclaredField(property.getName());
                String propertyName = property.getName();
                Object value = property.getValue();
                String ref = property.getRef();

                Method method = clazz.getMethod(
                    "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1),
                    new Class[] {field.getType()});

                if (ref != null) {
                    Object refBean = getBean(ref);
                    method.invoke(bean, refBean);
                } else {
                    //Integer와 String 필드에 대해서만 동작
                    if ("java.lang.Integer".equals(field.getType().getName())) {
                        method.invoke(bean, Integer.parseInt(value.toString()));
                    } else {
                        method.invoke(bean, value.toString());
                    }
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Cannot instantiate [bean name : " + beanName
                    + "]; is not have field [" + property.getName() + "]");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Cannot instantiate [bean name : " + beanName
                    + "]; Cannot access field [" + property.getName() + "]");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Cannot instantiate [bean name : " + beanName
                    + "]; Cannot access field, set method not defined [" + property.getName() + "]");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private Object callLifecycleMethodsIfNecessary(Object bean, String key) {

        if (bean instanceof BeanNameAware) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invoking setBeanName() on BeanNameAware bean '" + key + "'");
            }
            ((BeanNameAware)bean).setBeanName(key);
        }

        if (bean instanceof BeanFactoryAware) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invoking setBeanFactory() on BeanFactoryAware bean '" + key + "'");
            }
            ((BeanFactoryAware)bean).setBeanFactory(this);
        }

        bean = applyBeanPostProcessorBeforeInitialization(bean, key);

        if (bean instanceof InitializingBean) {
            ((InitializingBean)bean).afterPropertiesSet();
        }

        bean = applyBeanPostProcessorAfterInitialization(bean, key);

        return bean;
    }

    protected void clear() {
        this.beanHash.clear();
    }

    public Object applyBeanPostProcessorBeforeInitialization(Object bean, String name) {
        Object result = bean;
        for (Object o : getBeanPostProcessors()) {
            BeanPostProcessor beanPostProcessor = (BeanPostProcessor)o;
            result = beanPostProcessor.postProcessorBeforeInitialization(bean, name);
        }

        return result;
    }

    public Object applyBeanPostProcessorAfterInitialization(Object bean, String name) {
        Object result = bean;
        for (Object o : getBeanPostProcessors()) {
            BeanPostProcessor beanPostProcessor = (BeanPostProcessor)o;
            result = beanPostProcessor.postProcessorAfterInitialization(bean, name);
        }

        return result;
    }
}