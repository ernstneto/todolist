package br.com.ernstneto.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ernstneto.todolist.util.Utils;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity<Object> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        System.out.println("\n\nchegou no controller + request.getAttribute(idUser): " + request.getAttribute("idUser") + "\n\n");
        var idUser = request.getAttribute("idUser");
        taskModel.setUserId((UUID)idUser);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be in the future");
        }
        if(taskModel.getEndAt().isBefore(currentDate)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("End date must be in the future");
        }
        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }
    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        
        var tasks = this.taskRepository.findByUserId((UUID)idUser);
        return tasks;
    }
    //http://localhost:8080/tasks/id
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
        var idUser = request.getAttribute("idUser");
        
        var task = this.taskRepository.findById(id).orElse(null);
        if(task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task not found");
        }

        if (task.getUserId() == null || !task.getUserId().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão para alterar essa tarefa");
        }
        
        Utils.copyNonNullProperties(taskModel, task);
        //taskModel.setId((UUID)idUser);
        //taskModel.setId(id);
        System.out.println("\n\nchegou no controller + request.getAttribute(idUser): " + request.getAttribute("idUser") + "\n\n");
        //this.taskRepository.save(taskModel);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
        //return this.taskRepository.save(taskModel);
    }
    
    
}

