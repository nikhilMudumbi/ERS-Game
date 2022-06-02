public class CentralDeck extends Deck {
    
    Card bottomCard = null;
    Card topCard = null;
    Card secondCard = null;
    Card thirdCard = null;
    int topNum = 0;
    int secondNum = 0;
    int thirdNum = 0;
    int bottomNum = 0;


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
        topNum = topCard.getNumber();
        secondNum = secondCard.getNumber();
        thirdNum = thirdCard.getNumber();
        bottomNum = bottomCard.getNumber();
    }

    public void burnBottom(Card card) {
        cards.add(0,card);
        length++;
    }

    public void clear() {
        cards.clear();
        length = 0;
    }

    public boolean slappable() {
        if (doubleSlap()) return true;
        if (sandwichSlap()) return true;
        if (marriageSlap()) return true;
        if (divorceSlap()) return true;
        if (topBottomSlap()) return true;
        if (staircaseSlap()) return true;
        if (red10Slap()) return true;
        return false;
    }


    private boolean doubleSlap() {
        return topNum == secondNum;
    }

    private boolean sandwichSlap() {
        return topNum == thirdNum;
    }

    private boolean marriageSlap() {
        if (topNum == 13 && secondNum == 12) {
            return true;
        }
        if (topNum == 12 && secondNum == 13) {
            return true;
        }
        return false;
    }

    private boolean divorceSlap() {
        if (topNum == 13 && thirdNum == 12) {
            return true;
        }
        if (topNum == 12 && thirdNum == 13) {
            return true;
        }
        return false;
    }

    private boolean topBottomSlap() {
        return topNum == bottomNum;
    }

    private boolean staircaseSlap() {
        if  (topNum == 1 + secondNum && secondNum == 1 + thirdNum) {
            return true;
        }
        if (topNum == secondNum - 1 && secondNum == thirdNum - 1) {
            return true;
        }
        return false;
    }

    private boolean red10Slap() {
        return topNum == 10 && topCard.getColor().equals("red");
    }
}