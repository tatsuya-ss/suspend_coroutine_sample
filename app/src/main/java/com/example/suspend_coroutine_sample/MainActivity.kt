package com.example.suspend_coroutine_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suspend_coroutine_sample.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

class MainViewModel(): ViewModel() {

    private val _result: MutableLiveData<String> = MutableLiveData()
    val result: LiveData<String> = _result

    fun onCreate() {
        viewModelScope.launch {
            Query().execute(object : Query.Callback {
                override fun success(result: String) {
                    _result.value = result
                }

                override fun failure(e: Exception) {
                    _result.value = e.message
                }
            })
        }
    }
}

class Query {
    interface Callback {
        fun success(result: String)
        fun failure(e: Exception)
    }

    suspend fun execute(callback: Callback) {
        try {
            delay(1000L)
            callback.success("成功")
        } catch (e: Exception) {
            callback.failure(Exception("失敗"))
        }
    }
}