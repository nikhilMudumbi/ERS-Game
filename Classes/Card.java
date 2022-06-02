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
}