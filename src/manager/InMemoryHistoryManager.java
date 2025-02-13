package manager;

import data.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first; // первый элемент списка
    private Node last; // последний элемент списка

    protected static class Node {
        private final Task task;
        private Node next;
        private Node prev;

        public Node(Task task) {
            this.task = task;
        }
    }

    public void linkLast(Task task) {
        Node newNode = new Node(task);
        if (last == null) {
            first = newNode;
            last = newNode;
        } else {
            last.next = newNode;
            newNode.prev = last;
            last = newNode;
        }
        nodeMap.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            first = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            last = node.prev;
        }

        nodeMap.remove(node.task.getId()); // удалили узел из HashMap
    }

    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId())); // тут удаляем уже существующую задачу
        }
        linkLast(task); // добавляем в конец
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id); // Находим узел по id
        if (node != null) {
            removeNode(node); // Удаляем узел из списка и HashMap
        }
    }

    // Возвращаем список задач
    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node node = first;
        while (Objects.nonNull(node)) {
            result.add(node.task);
            node = node.next;
        }

        return result;
    }
}



