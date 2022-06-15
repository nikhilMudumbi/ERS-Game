public class PlayerDeck extends Deck {

    public PlayerDeck() {
        
    }

    public void addCard(Card card) {
        cards.add(0,card);
        length++;
    }

    public Card burnTop() {
        length--;
        return cards.remove(length);
    }
}