package com.cybertek.mapper;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.entity.Project;
import com.cybertek.repository.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    private ModelMapper modelMapper;
    private ProjectRepository projectRepository;

    public ProjectMapper(ModelMapper modelMapper, ProjectRepository projectRepository) {
        this.modelMapper = modelMapper;
        this.projectRepository = projectRepository;
    }

    public Project convertToEntity(ProjectDTO projectDTO) {

        return modelMapper.map(projectDTO, Project.class);
    }

    public ProjectDTO convertToDto(Project projectEntity) {
        return modelMapper.map(projectEntity, ProjectDTO.class);
    }
}
