package com.example.composabletasks.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.composabletasks.service.model.TaskModel
import com.example.composabletasks.service.model.ValidationModel
import com.example.composabletasks.service.repository.PriorityRepository
import com.example.composabletasks.service.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskFormViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val priorityRepository: PriorityRepository = PriorityRepository(application.applicationContext)
    private val taskRepository = TaskRepository(application.applicationContext)

    val priorityList = priorityRepository.list().asLiveData()

    private val _taskSaved = MutableLiveData<ValidationModel>()
    val taskSaved: LiveData<ValidationModel> = _taskSaved

    private val _task = MutableLiveData<TaskModel>()
    val task: LiveData<TaskModel> = _task

    private val _taskLoad = MutableLiveData<ValidationModel>()
    val taskLoad: LiveData<ValidationModel> = _taskLoad

    fun save(task: TaskModel) {
        viewModelScope.launch {
            try {
                val response = if (task.id == 0) {
                    taskRepository.save(task)
                } else {
                    taskRepository.update(task)
                }
                if (response.isSuccessful && response.body() != null) {
                    _taskSaved.value = ValidationModel()
                } else {
                    _taskSaved.value = parseErrorMessage(response)
                }
            } catch (e: Exception) {
                _taskSaved.value = handleException(e)
            }
        }
    }

    fun load(taskId: Int) {
        viewModelScope.launch {
            try {
                val response = taskRepository.load(taskId)
                if (response.isSuccessful && response.body() != null) {
                    _task.value = response.body()!!
                } else {
                    _taskLoad.value = parseErrorMessage(response)
                }
            } catch (e: Exception) {
                _taskSaved.value = handleException(e)
            }
        }
    }

}