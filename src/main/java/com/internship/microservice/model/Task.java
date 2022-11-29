package com.internship.microservice.model;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tasks_databases",
            joinColumns = @JoinColumn(name = "task_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "database_id", nullable = false)
    )
    private Set<Database> targetDatabases;

    // sql statement to execute
    @Column(nullable = false)
    private String sqlAction;

    private Instant executedAt;

    public Task() {
    }

    public Task(Set<Database> targetDatabases, String sqlAction) {
        this.targetDatabases = targetDatabases;
        this.sqlAction = sqlAction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Database> getTargetDatabases() {
        return targetDatabases;
    }

    public void setTargetDatabases(Set<Database> targetDatabases) {
        this.targetDatabases = targetDatabases;
    }

    public String getSqlAction() {
        return sqlAction;
    }

    public void setSqlAction(String sqlAction) {
        this.sqlAction = sqlAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        Set<Long> databasesIds = targetDatabases.stream()
                .map(Database::getId)
                .collect(Collectors.toSet());

        return "Task{" +
                "id=" + id +
                ", targetDatabases='" + databasesIds + '\'' +
                ", sqlAction='" + sqlAction + '\'' +
                ", executedAt=" + executedAt +
                '}';
    }
}
