package com.example.composabletasks.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    // Variáveis de Estado para o Compose
    var id by mutableIntStateOf(0)
        private set
    var description by mutableStateOf("")
        private set
    fun onDescriptionChange(newDesc: String) { description = newDesc }

    var priorityId by mutableIntStateOf(0)
        private set
    fun onPriorityChange(newId: Int) { priorityId = newId }

    var dueDate by mutableStateOf("")
        private set
    fun onDueDateChange(newDate: String) { dueDate = newDate }

    var complete by mutableStateOf(false)
        private set
    fun onTaskCompletedChange(newStatus: Boolean) { complete = newStatus }

    private val _taskSaved = MutableLiveData<ValidationModel>()
    val taskSaved: LiveData<ValidationModel> = _taskSaved

    private val _task = MutableLiveData<TaskModel>()
    val task: LiveData<TaskModel> = _task

    private val _taskLoad = MutableLiveData<ValidationModel>()
    val taskLoad: LiveData<ValidationModel> = _taskLoad

    fun save() {
        val task = TaskModel(
            id = this.id,
            priorityId = this.priorityId,
            description = this.description,
            dueDate = this.dueDate,
            complete = this.complete
        )

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
                    val loadedTask = response.body()!!

                    // Alimenta os estados para a tela refletir imediatamente
                    id = loadedTask.id
                    description = loadedTask.description
                    priorityId = loadedTask.priorityId
                    dueDate = loadedTask.dueDate
                    complete = loadedTask.complete

                } else {
                    _taskLoad.value = parseErrorMessage(response)
                }
            } catch (e: Exception) {
                _taskSaved.value = handleException(e)
            }
        }
    }

}