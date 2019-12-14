package com.officesmart.core.common

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotNull

/**
 *
 * BaseDocument
 *
 * @author Burgess Li
 * @date 11/20/2019 4:01 PM
 * @version 1.0
 *
 */
@Document
abstract class BaseDocument {

    static final class Field {
        static String CREATED_DATE = 'createdDate'
        static String LAST_MODIFIED_DATE = 'lastModifiedDate'
    }

    @Id
    String id

    @NotNull
    @CreatedDate
    Date createdDate = new Date()

    @NotNull
    @LastModifiedDate
    Date lastModifiedDate = new Date()

}