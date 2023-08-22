package com.qi.billiards.http

/**
 * {
 *     "isok": true,
 *     "code": 200,
 *     "message": "请求响应成功",
 *     "data": null
 * }
 */
data class AjaxResponse<T>(
    val isok: Boolean,
    val code: Int,
    val message: String,
    val data: List<T>?,
)
