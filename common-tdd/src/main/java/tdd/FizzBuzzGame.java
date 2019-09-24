package tdd;

import static java.lang.String.valueOf;

public class FizzBuzzGame {

    private int raw;

    public FizzBuzzGame(int raw) {

        this.raw = raw;
    }

    @Override
    public String toString() {
        if (isRelatedTo(3) && isRelatedTo(5)) {
            return "FizzBuzz";
        }
        if (isRelatedTo(3)) {
            return "Fizz";
        }
        if (isRelatedTo(5)) {
            return "Buzz";
        }
        return valueOf(raw);
    }


    public boolean isRelatedTo(int num) {
        return raw % num == 0 || valueOf(raw).contains(valueOf(num));
    }
}
