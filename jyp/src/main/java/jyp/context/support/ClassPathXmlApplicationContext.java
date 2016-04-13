package jyp.context.support;

import jyp.context.ApplicationContext;
import org.springframework.beans.BeansException;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 13.
 */
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {

    private String[] configLocations;

    /**
     * Create a new ClassPathXmlApplicationContext, loading the definitions
     * from the given XML file.
     * @param configLocation file path
     */
    public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
        this.configLocations = new String[] {configLocation};
        refresh();
    }

    /**
     * Create a new ClassPathXmlApplicationContext with the given parent,
     * loading the definitions from the given XML files.
     * @param configLocations array of file paths
     * @param parent the parent context
     */
    public ClassPathXmlApplicationContext(String[] configLocations, ApplicationContext parent)
            throws BeansException {
        super(parent);
        this.configLocations = configLocations;
        refresh();
    }

    @Override
    protected String[] getConfigLocations() {
        return this.configLocations;
    }
}
