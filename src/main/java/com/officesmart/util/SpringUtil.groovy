package com.officesmart.util

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component


@Component
class SpringUtil implements ApplicationContextAware {
  private static ApplicationContext applicationContext = null

  @Override
  void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    if (SpringUtil.applicationContext == null) {
      synchronized (SpringUtil.class) {
        if (SpringUtil.applicationContext == null) {
          SpringUtil.applicationContext = applicationContext
        }
      }
    }
  }

  static ApplicationContext getApplicationContext() {
    return applicationContext
  }

  static Object getBean(String name){
    return getApplicationContext().getBean(name)
  }

  static <T> T getBean(Class<T> clazz){
    return getApplicationContext().getBean(clazz)
  }

  static <T> T getBean(String name, Class<T> clazz){
    return getApplicationContext().getBean(name, clazz)
  }
}
