package basicUtil;

/**
 * Created by jiachen on 29/12/15.
 */
public class Pair<K, V> {
    K pre;
    V next;
    public Pair(K pre, V next) {
        this.pre = pre;
        this.next = next;
    }

    public K first() {
        return pre;
    }

    public V second() {
        return next;
    }

    public void setFirst(K pre) {
        this.pre = pre;
    }

    public void setSecond(V next) {
        this.next = next;
    }
}
