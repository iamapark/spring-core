package jyp.beans.factory;

/**
 * @author jinyoung.park89
 * @since 2016. 4. 20.
 */
public interface DisposableBean {

    void destroy() throws Exception;
}
