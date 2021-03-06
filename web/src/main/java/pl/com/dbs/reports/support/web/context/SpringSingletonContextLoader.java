/*
 * Created on 2006-03-29
 */
package pl.com.dbs.reports.support.web.context;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Ladowacz konktekstu springa, ktory uzywa SingletonBeanFactoryLocator zamiast
 * ContextSingletonBeanFactoryLocator.
 * @see web.xml 
 * 
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @copyright (c) 2013
 */
public class SpringSingletonContextLoader extends ContextLoader {
    private final Logger logger = LoggerFactory.getLogger(SpringSingletonContextLoader.class);
    private BeanFactoryReference parentFactoryRef;

    protected ApplicationContext loadParentContext(ServletContext servletContext) throws BeansException {
    	// print logback's internal status
    	LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);    	
    	
        String locatorFactorySelector = servletContext.getInitParameter(LOCATOR_FACTORY_SELECTOR_PARAM);
        String parentContextKey = servletContext.getInitParameter(LOCATOR_FACTORY_KEY_PARAM);

        if (parentContextKey != null) {
            BeanFactoryLocator locator = (locatorFactorySelector != null) ?
                SingletonBeanFactoryLocator.getInstance(locatorFactorySelector) :
                    SingletonBeanFactoryLocator.getInstance();
    
            if (logger.isInfoEnabled()) {
                logger.info("Getting parent context definition: using parent context key of '" +
                        parentContextKey + "' with BeanFactoryLocator");
            }
    
            this.parentFactoryRef = locator.useBeanFactory(parentContextKey);
            return (ApplicationContext) this.parentFactoryRef.getFactory();
        } else {
            return null;
        }
    }

    public void closeWebApplicationContext(ServletContext servletContext) {
        try {
            super.closeWebApplicationContext(servletContext);
        } finally {
            if (this.parentFactoryRef != null) {
                this.parentFactoryRef.release();
            }
        }
    }
}
