package Classes;

public class PlayerDeck extends Deck {
    
    public void removeTop() {
        cards.remove(cards.size()-1);
        length--;
    }

    public void addDeck(CentralDeck deck) {
        for (Card card : deck.cards) {
            cards.add(0,card);
            length++;
        }
    }
}