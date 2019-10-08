package al.learn;

/**
 * @author: Pantheon
 * @date: 2019/9/29 14:54
 * @comment: 单链表实现lru
 */
public class Lru<T> {


    private Node head;

    private int count;

    public Node add(T num) {
        Node node = new Node(num);
        if (head == null) {
            head = node;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = node;
        }
        count++;
        return node;
    }

    public int size() {
        return count;
    }

    public boolean contains(T num) {
        boolean flag = false;
        Node current = head;
        while (current != null) {
            if (current.data.equals(num)) {
                flag = true;
                break;
            }
            current = current.next;
        }

        return flag;
    }

    public boolean delete(Node node) {
        if (node == head) {
            head = head.next;
        }
        Node current = head;
        while (current.next != null && current.next.data != node.data) {
            current = current.next;
        }
        if (current == null) {
            return false;
        }
        current.next = current.next.next;
        return true;
    }


    public boolean deleteByData(T data) {
        if (head.data == data) {
            head = head.next;
        }

        return false;
    }

    public class Node<T> {
        private T data;
        private Node next;

        public Node(T data) {
            this.data = data;
        }
    }
}
