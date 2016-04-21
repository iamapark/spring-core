package jyp.beans.factory.support;

import jyp.ConstructorArgumentValues;
import jyp.PropertyValues;

public class RootBeanDefinition extends AbstractBeanDefinition {

    private final Class clazz;
    private final ConstructorArgumentValues constructorArgumentValues;

    public RootBeanDefinition(Class clazz, PropertyValues propertyValues,
            ConstructorArgumentValues constructorArgumentValues) {
        super(propertyValues);
        this.clazz = clazz;
        this.constructorArgumentValues = constructorArgumentValues;
    }

    public Class getBeanClass() {
        return clazz;
    }

    public ConstructorArgumentValues getConstructorArgumentValues() {
        return constructorArgumentValues;
    }

    public boolean isCreateWithConstructor() {
        return this.constructorArgumentValues != null
            && this.constructorArgumentValues.hasConstructorArguments();
    }
}
