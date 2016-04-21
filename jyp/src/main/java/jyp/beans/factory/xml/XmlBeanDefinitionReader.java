package jyp.beans.factory.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jyp.ConstructorArgument;
import jyp.beans.PropertyValue;
import jyp.beans.factory.config.ConstructorArgumentValues;
import jyp.beans.PropertyValues;
import jyp.beans.factory.support.AbstractBeanDefinitionReader;
import jyp.beans.factory.support.BeanDefinitionReader;
import jyp.beans.factory.support.BeanDefinitionRegistry;
import jyp.beans.factory.support.RootBeanDefinition;
import jyp.core.io.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    private static final String BEAN_ELEMENT = "bean";
    private static final String CLASS_ATTRIBUTE = "class";
    private static final String ID_ATTRIBUTE = "id";
    private static final String PROPERTY_ELEMENT = "property";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String CONSTRUCTOR_ARG = "constructor-arg";
    private static final String REF_ATTRIBUTE = "ref";
    protected final Log logger = LogFactory.getLog(getClass());

    /*private final BeanDefinitionRegistry beanDefinitionRegistry;*/

    private Class parserClass = DefaultXmlBeanDefinitionParser.class;

    public XmlBeanDefinitionReader(BeanDefinitionRegistry beanFactory) {
        super(beanFactory);
    }


    public void loadBeanDefinitions(Resource resource) {
        try {
            loadBeanDefinitions(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadBeanDefinitions(InputStream inputStream) {
        if (inputStream == null)
            throw new IllegalArgumentException("InputStream cannot be null: expected an XML file");

        try (InputStream is = inputStream) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            loadBeanDefinitions(doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Parsing XML Exception !");

        } catch (SAXException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid XML Document !");

        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Parsing XML Document Exception !");
        }
    }

    private void loadBeanDefinitions(Document doc) {
        Element root = doc.getDocumentElement();
        NodeList nl = root.getElementsByTagName(BEAN_ELEMENT);

        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            loadBeanDefinition((Element)n);
        }
    }

    private void loadBeanDefinition(Element element) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        if (id == null || "".equals(id))
            throw new IllegalArgumentException("Bean without id attribute");

        PropertyValues propertyValues = createPropertyValues(element);
        ConstructorArgumentValues constructorArgument = createConstructorArgument(element);
        RootBeanDefinition rootBeanDefinition = createBeanDefinition(element, id, propertyValues,
            constructorArgument);
        this.getBeanFactory().registerBeanDefinition(id, rootBeanDefinition);
        /*beanDefinitionRegistry.registerBeanDefinition(id, rootBeanDefinition);*/
    }

    private ConstructorArgumentValues createConstructorArgument(Element element) {

        NodeList childNodes = element.getElementsByTagName(CONSTRUCTOR_ARG);
        if (childNodes.getLength() == 0) {
            return null;
        }

        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        for (int i=0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            String refName = ((Element) item).getAttribute(REF_ATTRIBUTE);
            constructorArgumentValues.addConstructorArgument(new ConstructorArgument(refName));
        }

        return constructorArgumentValues;
    }

    private PropertyValue createPropertyValue(Element propElement) {
        String propertyName = propElement.getAttribute(NAME_ATTRIBUTE);
        if (propertyName == null || "".equals(propertyName)) {
            throw new IllegalArgumentException("Property without a name");
        }

        Object value = getValue(propElement);
        String ref = getRef(propElement);

        if (value != null && ref != null) {
            throw new IllegalArgumentException(
                "Property has only one of value and ref, value: " + value + ", ref: " + ref);
        }

        return new PropertyValue(propertyName, value, ref);
    }

    private Object getValue(Element propElement) {
        //nested value, 예를들어 List, Map등의 판별을 통해 데이터를 만들어내야 하지만
        //학습을 위한 구현이므로 하나의 데이터를 저장하게끔 구현한다
        String valueAttribute = propElement.getAttribute(VALUE_ATTRIBUTE);
        if (valueAttribute == null || valueAttribute.isEmpty()) {
            return null;
        }
        return valueAttribute;
    }

    private String getRef(Element propElement) {
        String refAttribute = propElement.getAttribute(REF_ATTRIBUTE);
        if (refAttribute == null || refAttribute.isEmpty()) {
            return null;
        }
        return refAttribute;
    }

    private PropertyValues createPropertyValues(Element beanElement) {
        PropertyValues propertyValues = new PropertyValues();
        NodeList nl = beanElement.getElementsByTagName(PROPERTY_ELEMENT);
        for (int i = 0; i < nl.getLength(); i++) {
            Element propElement = (Element)nl.item(i);
            PropertyValue propertyValue = createPropertyValue(propElement);
            propertyValues.addPropertyValue(propertyValue);
        }

        return propertyValues;
    }

    private RootBeanDefinition createBeanDefinition(Element element,
                                                    String id,
                                                    PropertyValues propertyValues,
                                                    ConstructorArgumentValues constructorArgumentValues) {
        if (!element.hasAttribute(CLASS_ATTRIBUTE))
            throw new IllegalArgumentException("Bean without class attribute");
        String classname = element.getAttribute(CLASS_ATTRIBUTE);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            return new RootBeanDefinition(Class.forName(classname, true, classLoader), propertyValues,
                constructorArgumentValues);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(
                "Error creating bean with name [" + id + "]: class '" + classname + "' not found", e);
        }
    }

    private void registerBeanDefinitions(Document doc, Resource resource)
            throws IllegalAccessException, InstantiationException {
        XmlBeanDefinitionParser parser = (XmlBeanDefinitionParser)this.parserClass.newInstance();
        parser.registerBeanDefinitions(getBeanFactory(), getBeanClassLoader(), doc, resource);
    }
}
