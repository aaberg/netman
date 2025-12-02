package netman.access

import io.micronaut.serde.ObjectMapper
import jakarta.inject.Singleton
import netman.access.repository.TaskDTO
import netman.access.repository.TaskRepository
import netman.access.repository.TriggerDTO
import netman.access.repository.TriggerRepository
import netman.models.Task
import netman.models.TaskStatus
import netman.models.TaskType
import netman.models.Trigger
import netman.models.TriggerStatus
import java.time.Instant
import java.util.UUID

@Singleton
open class TaskAccess(
    private val taskRepository: TaskRepository,
    private val triggerRepository: TriggerRepository,
    private val objectMapper: ObjectMapper
) {

    fun saveTask(task: Task): Task {
        val isNewTask = task.id == null || !taskRepository.existsById(task.id)
        val taskDto = TaskDTO(
            id = task.id ?: UUID.randomUUID(),
            userId = task.userId,
            data = objectMapper.writeValueAsString(task.data),
            status = task.status.name,
            created = task.created ?: Instant.now()
        )
        
        val savedTaskDto = if (isNewTask) {
            taskRepository.save(taskDto)
        } else {
            taskRepository.update(taskDto)
        }
        
        return mapTask(savedTaskDto)
    }

    fun getTask(taskId: UUID): Task? {
        val taskDto = taskRepository.getById(taskId)
        return taskDto?.let { mapTask(it) }
    }

    fun getTasksByUserId(userId: UUID): List<Task> {
        return taskRepository.findByUserId(userId).map { mapTask(it) }
    }

    fun saveTrigger(trigger: Trigger): Trigger {
        val isNewTrigger = trigger.id == null || !triggerRepository.existsById(trigger.id)
        val triggerDto = TriggerDTO(
            id = trigger.id ?: UUID.randomUUID(),
            triggerType = trigger.triggerType,
            triggerTime = trigger.triggerTime,
            targetTaskId = trigger.targetTaskId,
            status = trigger.status.name,
            statusTime = trigger.statusTime
        )
        
        val savedTriggerDto = if (isNewTrigger) {
            triggerRepository.save(triggerDto)
        } else {
            triggerRepository.update(triggerDto)
        }
        
        return mapTrigger(savedTriggerDto)
    }

    fun getTrigger(triggerId: UUID): Trigger? {
        val triggerDto = triggerRepository.getById(triggerId)
        return triggerDto?.let { mapTrigger(it) }
    }

    fun getTriggersByTaskId(taskId: UUID): List<Trigger> {
        return triggerRepository.findByTargetTaskId(taskId).map { mapTrigger(it) }
    }

    fun getTriggersByStatusAndTime(status: TriggerStatus, currentTime: Instant): List<Trigger> {
        return triggerRepository.findByStatusAndTriggerTimeBefore(status.name, currentTime).map { mapTrigger(it) }
    }

    private fun mapTask(taskDto: TaskDTO): Task {
        val taskData = objectMapper.readValue(taskDto.data, TaskType::class.java)
        return Task(
            id = taskDto.id,
            userId = taskDto.userId,
            data = taskData,
            status = TaskStatus.valueOf(taskDto.status),
            created = taskDto.created
        )
    }

    private fun mapTrigger(triggerDto: TriggerDTO): Trigger {
        return Trigger(
            id = triggerDto.id,
            triggerType = triggerDto.triggerType,
            triggerTime = triggerDto.triggerTime,
            targetTaskId = triggerDto.targetTaskId,
            status = TriggerStatus.valueOf(triggerDto.status),
            statusTime = triggerDto.statusTime
        )
    }
}
