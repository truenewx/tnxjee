package org.truenewx.tnxjee.web.view.thymeleaf.dialect;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.truenewx.tnxjee.Framework;
import org.truenewx.tnxjee.web.view.thymeleaf.processor.ThymeleafProcessor;

/**
 * 自定义的Thymeleaf方言
 */
@Component
public class ThymeleafDialect extends AbstractProcessorDialect implements ApplicationContextAware {

    public static final String NAME = Framework.NAME;
    public static final String PREFIX = "tnx";
    public static final int PROCESSOR_PRECEDENCE = StandardDialect.PROCESSOR_PRECEDENCE + 1;

    private ApplicationContext context;

    protected ThymeleafDialect(String name, String prefix, int processorPrecedence) {
        super(name, prefix, processorPrecedence);
    }

    public ThymeleafDialect() {
        this(NAME, PREFIX, PROCESSOR_PRECEDENCE);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> processors = new HashSet<>();
        if (getPrefix().equals(dialectPrefix)) {
            this.context.getBeansOfType(ThymeleafProcessor.class).values().forEach(processor -> {
                if (processor.getDialectPrefix().equals(dialectPrefix)) {
                    processors.add(processor);
                }
            });
        }
        return processors;
    }
}
