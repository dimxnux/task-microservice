package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.model.Database;
import com.internship.microservice.model.Task;
import com.internship.microservice.repository.DatabaseRepository;
import com.internship.microservice.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final TaskRepository taskRepository;
    private final DatabaseRepository databaseRepository;
    private final RoutingDataSource routingDataSource;
    private final JdbcTemplate jdbcTemplate;

    public TaskService(TaskRepository taskRepository, DatabaseRepository databaseRepository,
                       RoutingDataSource routingDataSource, JdbcTemplate jdbcTemplate) {
        this.taskRepository = taskRepository;
        this.databaseRepository = databaseRepository;
        this.routingDataSource = routingDataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

//     get the not yet executed tasks that must execute now (at the scheduled time)
    @Scheduled(fixedRateString = "${app.task-execution-delay}")
    private void runScheduledTasks() throws SQLException {
        routingDataSource.setSettingsTargetDataSource();
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);
        Set<Task> tasksToRun = taskRepository.findAllByExecutedAtIsNull();

        if (tasksToRun.isEmpty()) {
            return;
        }

        for (Task task : tasksToRun) {
            Set<Database> targetDatabases = task.getTargetDatabases();
            routingDataSource.setTargetDataSourcesFromDatabases(targetDatabases);

            for (Database database : targetDatabases) {
                DataSourceContextHolder.setContext(String.valueOf(database.getId()));
                int rowsUpdated = jdbcTemplate.update(task.getSqlAction());
                // maybe check for rowsUpdated == 0

                routingDataSource.closeCurrentContextDataSource();
            }
            routingDataSource.setSettingsTargetDataSource();
            DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);
            taskRepository.markTaskAsExecuted(task.getId());
        }
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
