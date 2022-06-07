public class PlayerDeck extends Deck {
    
    public Card removeTop() {
        Card card = cards.get(cards.size()-1);
        cards.remove(cards.size()-1);
        length--;
        return card;
    }

    public void addDeck(CentralDeck deck) {
        for (Card card : deck.cards) {
            cards.add(0,card);
            length++;
        }
    }
}