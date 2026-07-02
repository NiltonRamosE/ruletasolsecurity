package com.nramos.ruletasolsecurity.network

import com.nramos.ruletasolsecurity.data.remote.CategoryResponse
import retrofit2.http.GET

interface ApiService {
    @GET("api/ruleta/all")
    suspend fun getCategories(): List<CategoryResponse>
}