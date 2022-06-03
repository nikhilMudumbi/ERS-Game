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
            secondNum = secondCard.getNumber();
        }
        if (length > 2) {
            thirdCard = cards.get(length-3);
            thirdNum = thirdCard.getNumber();
        }
        topNum = topCard.getNumber();
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

    //valid slap

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

    //combinations

    private boolean doubleSlap() {
        return topNum == secondNum;
    }

    private boolean sandwichSlap() {
        return topNum == thirdNum;
    }

    private boolean topBottomSlap() {
        return topNum == bottomNum && cards.size() > 1;
    }

    private boolean red10Slap() {
        return topNum == 10 && topCard.getColor().equals("red");
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

    private boolean staircaseSlap() {
        if  (topNum == 1 + secondNum && secondNum == 1 + thirdNum && topNum <= 10) {
            return true;
        }
        if (topNum == secondNum - 1 && secondNum == thirdNum - 1 && thirdNum <= 10) {
            return true;
        }
        return false;
    }

    //face cards
    public void faceCards () {
        if (topNum == 11) {
            //nextTurn.topNum
        }

        if (topNum == 12) {
            //next 2 turns .isFaceCard() - get cards
            //false
        }

        if (topNum == 13) {
            //next 3 turns
        }

        if (topNum == 14) {
            //next 4 cards placed
        }
    }
    

}