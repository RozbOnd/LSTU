package TuringMachine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Getter
public class Tape {
    private Deque<Character> leftTape, rightTape;
    private char blank;
    private int headPos;

    public Tape(String start, char blank) {
        leftTape = new ArrayDeque<>();
        rightTape = new ArrayDeque<>();
        headPos = 0;
        for (Character c : start.toCharArray()) {
            rightTape.addLast(c);
        }
        if (rightTape.isEmpty()) rightTape.addLast(blank);
        this.blank = blank;
    }

    public Tape() {}

    public Tape(Tape other) {
        this.leftTape = new ArrayDeque<>(other.leftTape);
        this.rightTape = new ArrayDeque<>(other.rightTape);
        this.blank = other.blank;
        this.headPos = other.headPos;
    }

    public Character read() {
        return (!rightTape.isEmpty() ? rightTape.getFirst() : blank);
    }

    public void write(Character c) {
        if (rightTape.isEmpty()) rightTape.addFirst(blank);
        rightTape.removeFirst();
        rightTape.addFirst(c);
    }

    public void rightMove() {
        if (rightTape.isEmpty()) rightTape.addFirst(blank);
        else leftTape.addLast(rightTape.removeFirst());
        headPos++;
    }

    public void leftMove() {
        if (leftTape.isEmpty()) rightTape.addFirst(blank);
        else rightTape.addFirst(leftTape.removeLast());
        headPos--;
    }

    @JsonIgnore
    public List<Character> getFullTape() {
        List<Character> fullTape = new ArrayList<>();
        fullTape.addAll(leftTape);
        fullTape.addAll(rightTape);
        return fullTape;
    }

    @Override
    public String toString() {
        return getFullTape().toString();
    }

    public int getHeadPos() {
        return leftTape.size();
    }
}
