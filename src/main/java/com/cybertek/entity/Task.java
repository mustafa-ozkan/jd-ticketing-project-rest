package com.cybertek.entity;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@Where(clause = "is_deleted=false")
public class Task extends BaseEntity {

    private String taskSubject;
    private String taskDetail;
    @Enumerated(EnumType.STRING)
    private Status taskStatus;

    private LocalDate assignedDate;

    //one user can have many tasks
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User assignedEmployee;

    //one project can have many tasks
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
}
