package data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {



    @Test
    void TaskInstancesEqualIfTheirIDEqualTest() {
        Task task = new Task("Task1","paint green button",TaskStatus.NEW);
        task.setId(1);
        Task task2 = new Task("Task1","paint green button",TaskStatus.NEW);
        task2.setId(1);
        assertEquals(task,task2,"счетчик Task работает не корректно");
        assertEquals(task.getId(),task2.getId(),"Айди Task разные");
    }

    @Test
    void setId() {
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        task.setId(148);
        assertEquals(148,task.getId(),"установка ID таксков работает не корректно");
    }
}