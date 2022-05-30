package Classes;

public class CentralDeck extends Deck {
    
    Card bottomCard = null;
    Card topCard = null;
    Card secondCard = null;
    Card thirdCard = null;

    public void addTop(Card card) {
        cards.add(card);
        length++;
        if (length == 1) {
            bottomCard = card;
        }
        topCard = cards.get(length-1);
        if (length > 1) {
            secondCard = cards.get(length-2);
        }
        if (length > 2) {
            thirdCard = cards.get(length-3);
        }
    }

    public void burnBottom(Card card) {
        cards.add(0,card);
        length++;
    }

    public void clear() {
        cards.clear();
        length = 0;
    }
}