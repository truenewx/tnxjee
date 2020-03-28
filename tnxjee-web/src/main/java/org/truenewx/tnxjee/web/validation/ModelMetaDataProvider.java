package org.truenewx.tnxjee.web.validation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.location.FieldConstraintLocation;
import org.hibernate.validator.internal.metadata.provider.AnnotationMetaDataProvider;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.validation.annotation.InheritConstraint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ModelMetaDataProvider implements MetaDataProvider {

    private AnnotationMetaDataProvider delegate;

    @Autowired
    public void setLocalValidatorFactoryBean(LocalValidatorFactoryBean validator) {
        ValidatorImpl targetValidator = BeanUtil.getFieldValue(validator, "targetValidator");
        BeanMetaDataManager beanMetaDataManager = BeanUtil.getFieldValue(targetValidator, "beanMetaDataManager");
        List<MetaDataProvider> metaDataProviders = BeanUtil.getFieldValue(beanMetaDataManager, "metaDataProviders");
        for (int i = 0; i < metaDataProviders.size(); i++) {
            MetaDataProvider metaDataProvider = metaDataProviders.get(i);
            if (metaDataProvider instanceof AnnotationMetaDataProvider) {
                this.delegate = (AnnotationMetaDataProvider) metaDataProvider;
                metaDataProviders.set(i, this);
            }
        }
    }

    @Override
    public AnnotationProcessingOptions getAnnotationProcessingOptions() {
        return this.delegate.getAnnotationProcessingOptions();
    }

    @Override
    public <T> BeanConfiguration<? super T> getBeanConfiguration(Class<T> beanClass) {
        BeanConfiguration<? super T> configuration = this.delegate.getBeanConfiguration(beanClass);
        if (beanClass != CommandModel.class && CommandModel.class.isAssignableFrom(beanClass)) {
            Class<?> entityClass = ClassUtil.getActualGenericType(beanClass, CommandModel.class, 0);
            if (entityClass != null) {
                BeanConfiguration<?> entityConfiguration = this.delegate.getBeanConfiguration(entityClass);
                for (ConstrainedElement constrainedElement : configuration.getConstrainedElements()) {
                    if (constrainedElement instanceof ConstrainedField) {
                        ConstrainedField constrainedField = (ConstrainedField) constrainedElement;
                        Field field = constrainedField.getField();
                        Set<MetaConstraint<?>> entityFieldConstraints = getFieldMetaConstraints(entityConfiguration, field);
                        if (CollectionUtils.isNotEmpty(entityFieldConstraints)) {
                            Set<MetaConstraint<?>> constraints = new HashSet<>(constrainedField.getConstraints());
                            for (MetaConstraint<?> constraint : entityFieldConstraints) {
                                try {
                                    Constructor<FieldConstraintLocation> constructor = FieldConstraintLocation.class.getDeclaredConstructor(Field.class);
                                    constructor.setAccessible(true);
                                    FieldConstraintLocation location = constructor.newInstance(field);
                                    BeanUtil.setFieldValue(constraint, "location", location);
                                    constraints.add(constraint);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            BeanUtil.setFieldValue(constrainedField, "constraints", constraints);
                        }
                    }
                }
            }
        }
        return configuration;
    }

    private Set<MetaConstraint<?>> getFieldMetaConstraints(BeanConfiguration<?> configuration, Field field) {
        String fieldName = field.getName();
        InheritConstraint inheritConstraint = field.getAnnotation(InheritConstraint.class);
        if (inheritConstraint != null) {
            Class<? extends Entity> inheritedEntityClass = inheritConstraint.type();
            if (inheritedEntityClass != Entity.class && inheritedEntityClass != configuration.getBeanClass()) {
                configuration = this.delegate.getBeanConfiguration(inheritedEntityClass);
            }
            String inheritedFieldName = inheritConstraint.value();
            if (StringUtils.isNotBlank(inheritedFieldName)) {
                fieldName = inheritedFieldName;
            }
        }
        for (ConstrainedElement constrainedElement : configuration.getConstrainedElements()) {
            if (constrainedElement instanceof ConstrainedField) {
                ConstrainedField constrainedField = (ConstrainedField) constrainedElement;
                if (fieldName.equals(constrainedField.getField().getName())) {
                    return constrainedField.getConstraints();
                }
            }
        }
        return null;
    }

}
