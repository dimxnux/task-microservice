package com.internship.microservice.repository;

import com.internship.microservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Set;

@Repository("settingsTaskRepository")
public interface TaskRepository extends JpaRepository<Task, Long> {

//    @Query(value = "select t from Task t where t.executedAt is null")
//    Set<Task> getTasksNotExecutedYet();

    Set<Task> findAllByExecutedAtIsNull();

    @Query(value = "update Task t set t.executedAt = current_timestamp")
    @Modifying
    @Transactional
    void markTaskAsExecuted(Long taskId);
}
