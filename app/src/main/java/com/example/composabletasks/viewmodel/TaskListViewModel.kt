package com.example.composabletasks.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.composabletasks.service.constants.TaskConstants
import com.example.composabletasks.service.model.TaskModel
import com.example.composabletasks.service.model.ValidationModel
import com.example.composabletasks.service.repository.PriorityRepository
import com.example.composabletasks.service.repository.TaskRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de lista de tarefas.
 */
class TaskListViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val taskRepository = TaskRepository(application.applicationContext)
    private val priorityRepository = PriorityRepository(application.applicationContext)

    private val _tasks = MutableLiveData<List<TaskModel>>()
    val tasks: LiveData<List<TaskModel>> = _tasks

    private val _taskDeleted = MutableLiveData<ValidationModel>()
    val taskDeleted: LiveData<ValidationModel> = _taskDeleted

    private val _taskStatus = MutableLiveData<ValidationModel>()
    val taskStatus: LiveData<ValidationModel> = _taskStatus

    private var taskFilter = TaskConstants.FILTER.ALL

    fun list(filter: Int) {
        taskFilter = filter
        viewModelScope.launch {
            try {
                val response = when (filter) {
                    TaskConstants.FILTER.ALL -> taskRepository.list()
                    TaskConstants.FILTER.NEXT -> taskRepository.listNext()
                    else -> taskRepository.listOverdue()
                }

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!

                    result.map { task ->
                        task.priorityDescription = priorityRepository.getPriorityDescription(task.priorityId)
                    }

                    _tasks.value = result
                }
            } catch (e: Exception) {
                // Evita crash ao carregar a lista sem internet
            }
        }
    }

    fun status(taskId: Int, complete: Boolean) {
        viewModelScope.launch {
            try {
                val response = if (complete) {
                    taskRepository.complete(taskId)
                } else {
                    taskRepository.undo(taskId)
                }

                if (response.isSuccessful && response.body() != null) {
                    list(taskFilter)
                } else {
                    _taskStatus.value = parseErrorMessage(response)
                }
            } catch (e: Exception) {
                _taskStatus.value = handleException(e)
            }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            try {
                val response = taskRepository.delete(id)
                if (response.isSuccessful && response.body() != null) {
                    list(taskFilter)
                    _taskDeleted.value = ValidationModel()
                } else {
                    _taskDeleted.value = parseErrorMessage(response)
                }
            } catch (e: Exception) {
                _taskDeleted.value = handleException(e)
            }
        }
    }
}