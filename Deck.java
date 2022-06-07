import java.util.ArrayList;

public abstract class Deck {

    ArrayList<Card> cards = new ArrayList<>();
    int length = 0;

    public int getLength() {
        return length;
    }

    public Card getTop() { // maybe first check if deck is empty
        return cards.get(length-1);
    }

    public boolean isEmpty() {
        return length == 0;
    }

    public void addCard(Card card) {
        cards.add(card);
    }
}