package com.cybertek.mapper;

import com.cybertek.dto.TaskDTO;
import com.cybertek.entity.Task;
import org.dom4j.rule.Mode;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

//Add as a component
@Component
public class TaskMapper {
    // bring modelmapper
    ModelMapper modelMapper;

    //instead of autowire create a constructor
    public TaskMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Task convertToEntity(TaskDTO taskDTO){
        return modelMapper.map(taskDTO,Task.class);
    }

    public TaskDTO convertToDto(Task taskEntity){
        return modelMapper.map(taskEntity,TaskDTO.class);
    }
}
