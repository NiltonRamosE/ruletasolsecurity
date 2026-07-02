package com.nramos.ruletasolsecurity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nramos.ruletasolsecurity.data.Category
import com.nramos.ruletasolsecurity.network.RetrofitClient
import kotlinx.coroutines.launch

class RuletaViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = RetrofitClient.instance.getCategories()
                val categories = response.map { Category.fromResponse(it) }
                _categories.value = categories
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar las categorías"
            } finally {
                _isLoading.value = false
            }
        }
    }
}