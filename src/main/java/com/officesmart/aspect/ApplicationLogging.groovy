package com.officesmart.aspect

import groovy.util.logging.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest

@Aspect
@Configuration
@Slf4j
class ApplicationLogging {

    HttpServletRequest getHttpServletRequest () {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes()
        ServletRequestAttributes sra = (ServletRequestAttributes) ra
        return sra.getRequest()
    }

    @Around('execution(* com.cargosmart.cs2dashboard.core.*.*Controller.*(..))')
    Object loggingRequest(ProceedingJoinPoint joinPoint) {
        def startTime = System.currentTimeMillis(),
            type = joinPoint.getSignature().getDeclaringType(),
            method = type.getDeclaredMethods().find { it.name == joinPoint.getSignature().name },
            request = getHttpServletRequest(),
            result = joinPoint.proceed()
        def endTime = System.currentTimeMillis()
        log.info('{} from {} {}.{} in {}ms', request.getMethod(), request.getRemoteAddr(), type.getName(), method.getName(), (endTime - startTime))
        return result
    }

    @Around('execution(* com.cargosmart.cs2dashboard.core.*.*Processor.*(..))')
    Object loggingProcess(ProceedingJoinPoint joinPoint) {
        def startTime = System.currentTimeMillis(),
            exchange = joinPoint.getArgs()[0],
            exchangeId = exchange.getExchangeId(),
            destinationName = exchange.getFromEndpoint().getDestinationName(),
            result = joinPoint.proceed()
        def endTime = System.currentTimeMillis()
        log.info('[{}] received message, processed by [{}] in {}ms', destinationName, exchangeId, (endTime - startTime))
        return result
    }

}
