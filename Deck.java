import java.util.ArrayList;

public class Deck {

    ArrayList<Card> cards = new ArrayList<>();
    int length = 0;

    public int getLength() {
        return length;
    }

    public Card removeTop() {
        Card card = cards.get(cards.size()-1);
        cards.remove(cards.size()-1);
        length--;
        return card;
    }

    public Card getTop() { // maybe first check if deck is empty
        return cards.get(length-1);
    }

    public boolean isEmpty() {
        return length == 0;
    }

    public void addCard(Card card) {
        cards.add(card);
        length++;
    }
}