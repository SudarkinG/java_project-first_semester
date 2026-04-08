package com.mipt.sudarkingeorgiy.aspect;

import com.mipt.sudarkingeorgiy.repository.TaskReadRepository;
import com.mipt.sudarkingeorgiy.repository.TaskRepository;
import com.mipt.sudarkingeorgiy.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/** BeanPostProcessor пишет в лог до и после инициализации бинов TaskService и TaskRepository */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository || bean instanceof TaskReadRepository) {
            logger.info("Before initialization of bean '{}' of type {}", beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository || bean instanceof TaskReadRepository) {
            logger.info("After initialization of bean '{}' of type {}", beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }
}
