package com.example.suspend_coroutine_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suspend_coroutine_sample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel.onCreate()

        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.result.observe(this) {
            binding.textView.text = it
        }
    }
}

class MainViewModel: ViewModel() {

    private val _result: MutableLiveData<String> = MutableLiveData()
    val result: LiveData<String> = _result

    fun onCreate() {
        viewModelScope.launch {
            try {
                val result = Query().executeWithSuspendCoroutine()
                _result.value = result
            } catch (e: Exception) {
                _result.value = e.message
            }
        }
    }
}

class Query {
    suspend fun executeWithSuspendCoroutine() = suspendCoroutine<String> { continuation ->
        try {
            // 非同期の処理...
            continuation.resume("成功")
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
}