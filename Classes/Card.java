package Classes;

public class Card {
    private int number;
    // 1-13, ace-king
    private String suit;
    // spades, clubs, hearts, diamonds
    private String color;
    // red, black

    public Card(int number, String suit, String color) {
        this.number = number;
        this.suit = suit;
        this.color = color;

    }
    
    private String getColor() {
        return color;
    }

    private String getSuit() {
        return suit;
    }

    private int getNumber() {
        return number;
    }

    private boolean isFaceCard() {
        return number >= 11;
    }
}