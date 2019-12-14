package com.officesmart.core.common


import com.officesmart.util.SpringUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import java.beans.Introspector
import java.lang.reflect.ParameterizedType

abstract class BaseController<T extends BaseDocument, ID> {
    private Class<T> getTClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]
    }

    protected String getServiceBeanName () {
        return Introspector.decapitalize("${this.getTClass().getSimpleName()}Service")
    }

    protected BaseService<T, ID> getService() {
        return (BaseService<T, ID>) SpringUtil.getBean(this.getServiceBeanName())
    }

    @GetMapping
    ResponseEntity<PageResponse<T>> getAll (
            @RequestParam(defaultValue = '-1')Integer page,
            @RequestParam(defaultValue = '-1')Integer size,
            @RequestParam(defaultValue = 'lastModifiedDate') String sortBy,
            @RequestParam(defaultValue = 'DESC') Sort.Direction sortDirection
    ) {
        Sort sort = new Sort(new Sort.Order(sortDirection, sortBy))
        if (page < 0 || size < 0) {
            List<T> content = getService().findAll(sort).toList()
            return ResponseEntity.ok(new PageResponse<T>(
                    total: content.size(),
                    content: content
            ))
        }
        Page<T> resultSet = getService().findAll(PageRequest.of(page - 1, size, sort))
        return ResponseEntity.ok(new PageResponse<T>(
                page: page,
                size: size,
                total: resultSet.getTotalElements(),
                content: resultSet.getContent()
        ))
    }

    @GetMapping('/{id}')
    ResponseEntity<T> getById (@PathVariable ID id) {
        return ResponseEntity.ok(getService().findById(id))
    }

    @PostMapping
    ResponseEntity<T> save (@RequestBody T t) {
        return ResponseEntity.ok(getService().save(t))
    }

    @DeleteMapping('/{id}')
    ResponseEntity deleteById (@PathVariable ID id) {
        getService().deleteById(id)
        return new ResponseEntity(HttpStatus.OK)
    }

    @GetMapping('/mongo-stats')
    ResponseEntity<MongoCollectionStats> getMongoStats () {
        return ResponseEntity.ok(getService().getMongoCollectionStats())
    }
}
