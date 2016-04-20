package jyp.beans.factory.support;

import jyp.*;
import jyp.beans.PropertyValue;
import jyp.beans.PropertyValues;
import jyp.beans.factory.*;
import jyp.beans.factory.config.BeanDefinition;
import jyp.beans.factory.config.BeanPostProcessor;
import jyp.beans.factory.config.ConfigurableBeanFactory;
import jyp.beans.factory.config.ConstructorArgumentValues;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractBeanFactory implements ConfigurableBeanFactory, HierarchicalBeanFactory {

    private static final Object CURRENTLY_IN_CREATION = new Object();
    protected final Log logger = LogFactory.getLog(getClass());
    private final List beanPostProcessors = new ArrayList<>();
    /**
     * Map of Bean objects, keyed by id attribute
     */
    private final Map<String, Object> singletonCache = Collections.synchronizedMap(new HashMap<>());
    private BeanFactory parentBeanFactory;

    public AbstractBeanFactory(BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    public abstract BeanDefinition getBeanDefinition(String key);

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

    private Object getBeanInternal(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Bean name null is not allowed");
        }

        Object sharedInstance = singletonCache.get(name);
        if (sharedInstance != null) {
            if (sharedInstance == CURRENTLY_IN_CREATION) {
                throw new BeanCurrentlyInCreationException(
                    name + " Requested bean is already currently in creation");
            }
            if (logger.isDebugEnabled()) {
                logger.debug("returning cached object: " + name);
            }
            return sharedInstance;

        } else {
            RootBeanDefinition beanDefinition;
            try {
                beanDefinition = getMergedBeanDefinition(name);
            } catch (NoSuchBeanDefinitionException ex) {
                if (this.parentBeanFactory != null) {
                    return this.parentBeanFactory.getBean(name);
                }
                throw ex;
            }

            if (beanDefinition.isSingleton()) {
                synchronized (this.singletonCache) {
                    sharedInstance = this.singletonCache.get(name);
                    if (sharedInstance == null) {
                        logger.info("Creating shared instance of singleton bean '" + name + "'");
                        this.singletonCache.put(name, CURRENTLY_IN_CREATION);

                        try {
                            sharedInstance = createBean(name);
                            addSingleton(name, sharedInstance);
                        } catch (Exception ex) {
                            this.singletonCache.remove(name);
                            throw ex;
                        }
                    }
                }
                return sharedInstance;
            } else {
                // singleton이 아닐 경우 bean을 계속 생성한다.
                return createBean(name);
            }
        }
    }

    private RootBeanDefinition getMergedBeanDefinition(String name) {
        BeanDefinition beanDefinition = getBeanDefinition(name);
        if (beanDefinition == null) {
            throw new NoSuchBeanDefinitionException(name + " definition is required.");
        }
        return (RootBeanDefinition)beanDefinition;
    }

    private Object createBean(String beanName) {
        try {
            RootBeanDefinition beanDefinition = (RootBeanDefinition)getBeanDefinition(beanName);
            PropertyValues propertyValues = beanDefinition.getPropertyValues();

            Object bean;

            if (beanDefinition.isCreateWithConstructor()) {

                ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
                List<ConstructorArgument> constructorList = constructorArgumentValues.getConstructorArguments();
                Object[] refBeans = new Object[constructorList.size()];
                Class[] refBeanClass = new Class[constructorList.size()];

                for (int i = 0; i < constructorList.size(); i++) {
                    Object refBean = getBean(constructorList.get(i).getRefName());
                    refBeanClass[i] = refBean.getClass();
                    refBeans[i] = refBean;
                }

                Class beanClass = beanDefinition.getBeanClass();
                Constructor constructor = beanClass.getConstructor(refBeanClass);
                bean = constructor.newInstance(refBeans);

            } else {
                bean = beanDefinition.getBeanClass().newInstance();
            }

            applyPropertyValues(beanDefinition, propertyValues, bean, beanName);
            bean = callLifecycleMethodsIfNecessary(bean, beanName);

            if (beanDefinition.isSingleton()) {
                addSingleton(beanName, bean);
            }

            return bean;
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(
                "Cannot instantiate [bean name : " + beanName + "]; is it an interface or an abstract class?");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot instantiate [bean name : " + beanName
                + "]; has class definition changed? Is there a public constructor?");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot instantiate [bean name: " + beanName + "]");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot instantiate [bean name: " + beanName + "]");
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
        this.singletonCache.clear();
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

    @Override
    public BeanFactory getParentBeanFactory() {
        return this.parentBeanFactory;
    }

    @Override
    public void setParentBeanFactory(BeanFactory beanFactory) {
        this.parentBeanFactory = beanFactory;
    }

    //---------------------------------------------------------------------
    // Implementation of ConfigurableBeanFactory
    //---------------------------------------------------------------------
    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonCache) {
            Object oldObject = this.singletonCache.get(beanName);
            if (oldObject != null) {
                throw new BeanDefinitionStoreException("Could not register object [" + singletonObject +
                    "] under bean name '" + beanName + "': there's already object [" +
                    oldObject + " bound");
            }
            addSingleton(beanName, singletonObject);
        }
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        this.singletonCache.put(beanName, singletonObject);
    }

    @Override
    public void registerAlias(String beanName, String alias) {
        // Todo: 구현 필요!!
    }

    @Override
    public void ignoreDependencyType(Class type) {
        // Todo: 구현 필요!!
    }

    @Override
    public void destroySingletons() {
        if (logger.isInfoEnabled()) {
            logger.info("Destroying singletons in factory {" + this + "}");
        }

        synchronized (this.singletonCache) {
            Set<String> singletonCacheKeys = new HashSet<>(this.singletonCache.keySet());
            singletonCacheKeys.forEach(this::destroySingleton);
        }
    }

    protected void destroySingleton(String beanName) {
        Object singletonInstance = this.singletonCache.remove(beanName);
        if (singletonInstance != null) {
            destroyBean(beanName, singletonInstance);
        }
    }

    protected void destroyBean(String beanName, Object bean) {
        // Todo: 구현 필요!!
    }
}