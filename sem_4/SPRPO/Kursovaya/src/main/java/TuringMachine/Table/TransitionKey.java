package TuringMachine.Table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.util.Objects;

@Getter
public class TransitionKey {
    String state;
    Character currentChar;

    public TransitionKey(String state, Character currentChar) {
        this.state = state;
        this.currentChar = currentChar;
    }

    public TransitionKey() {};

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransitionKey that = (TransitionKey) o;
        return Objects.equals(state, that.state) &&
                Objects.equals(currentChar, that.currentChar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, currentChar);
    }

    @Override
    public String toString() {
        return state + "-" + currentChar;
    }

    @JsonCreator
    public static TransitionKey fromString(String val) {
        String[] parts = val.split("-");
        return new TransitionKey(parts[0], parts[1].charAt(0));
    }
}

