package com.officesmart.core.common

import com.alibaba.fastjson.JSONObject
import com.mongodb.BasicDBObject
import com.officesmart.util.SpringUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.CriteriaDefinition
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ControllerAdvice

import java.beans.Introspector
import java.lang.reflect.ParameterizedType

@Service
@ControllerAdvice
abstract class BaseService<T, ID> {
    protected Class<T> getTClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]
    }

    protected String getCollectionName() {
        return Introspector.decapitalize(this.getTClass().getSimpleName())
    }

    protected String getRepositoryBeanName() {
        return "${this.getCollectionName()}Repository"
    }

    protected PagingAndSortingRepository<T, ID> getRepository() {
        return (PagingAndSortingRepository<T, ID>) SpringUtil.getBean(this.getRepositoryBeanName())
    }

    final MongoCollectionStats getMongoCollectionStats() {
        return JSONObject.parseObject(SpringUtil.getBean(MongoTemplate.class).getDb().runCommand(new BasicDBObject(['collStats': this.getCollectionName(), 'scale': 1])).toJson(), MongoCollectionStats.class)
    }

    T findById(ID id) {
        Optional<T> optional = getRepository().findById(id)
        if (optional != null && optional.isPresent()) {
            return optional.get()
        } else {
            return null
        }
    }

    T save(T t) {
        if (t instanceof BaseDocument) {
            t.lastModifiedDate = new Date()
        }
        return getRepository().save(t)
    }

    Iterable<T> saveAll(Iterable<T> ts) {
        return getRepository().saveAll(ts)
    }

    void deleteById(ID id) {
        getRepository().deleteById(id)
    }

    void deleteAll(List<ID> ids) {
        ids.each { getRepository().deleteById(it) }
    }

    Iterable<T> findAll() {
        return getRepository().findAll()
    }

    Iterable<T> findAll(Sort sort) {
        return getRepository().findAll(sort)
    }

    Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable)
    }

    List count (List<CriteriaDefinition> criteriaDefinitions, String dateField, List<DateRange> dateRanges) {
        def result = []
        dateRanges.each { dateRange ->
            Query query = new Query()
            criteriaDefinitions.each { query.addCriteria(it) }
            result.add([
                    dateRange: dateRange,
                    count: SpringUtil.getBean(MongoTemplate.class).count(query.addCriteria(Criteria.where(dateField).gte(dateRange.start).lt(dateRange.end)), this.getTClass())
            ])
        }
        return result
    }
}
