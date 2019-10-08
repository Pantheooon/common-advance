package al.learn;

/**
 * @author: Pantheon
 * @date: 2019/9/29 17:02
 * @comment: 用链表判断是不是回文
 */
public class Plalindrome<T> {

    private Node head;

    private int count;

    public Plalindrome(String str) {
        add(str);
    }

    private void add(String str) {
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            Node node = new Node(aChar);
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
        }
    }

    public int size() {
        return count;
    }

    public boolean isPlalindrome() {
        Integer index = 0;
        Node current = head;
        while (current != null) {
            int correspondingIndex = count - 1 - index;
            Node node = getByIndex(correspondingIndex);
            if (node == null) {
                return false;
            }
            if (node.data.equals(current.data)) {
                index++;
                current = current.next;
            } else {
                return false;
            }
            if (index >= count / 2) {
                break;
            }
        }
        return true;
    }

    private Node getByIndex(int index) {
        int currentIndex = 0;
        Node current = head;
        while (current != null) {
            if (currentIndex == index) {
                return current;
            }
            current = current.next;
            currentIndex++;
        }
        return null;
    }


    public class Node<T> {
        private T data;
        private Node next;

        public Node(T data) {
            this.data = data;
        }
    }
}
