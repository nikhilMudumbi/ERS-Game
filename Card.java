public class Card {
    private int number;
    // 1-13, ace-king
    private String suit;
    // spades, clubs, hearts, diamonds
    private String color;
    // red, black
    private String[] faceList = {"jack", "queen", "king", "ace"};

    public Card(int number, String suit) {
        this.number = number;
        this.suit = suit;
        if (suit.equals("clubs") || suit.equals("spades")) {
            this.color = "black";
        }
        else {
            this.color = "red";
        }
    }
    
    public String getColor() {
        return color;
    }

    public String getSuit() {
        return suit;
    }

    public int getNumber() {
        return number;
    }

    public boolean isFaceCard() {
        return number >= 11;
    }

    @Override
    public String toString() {
        String str = "";
        if (number == 1) {
            str += "ace";
        }
        else if (number > 10) {
            str += faceList[number - 11];
        }
        else {
            str += number;
        }
        str += " of " + suit;
        if (suit.equals("clubs") || suit.equals("spades")) {
            str += ", black";
        }
        else {
            str += ", red";
        }
        return str;
    }
}