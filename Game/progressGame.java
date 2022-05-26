package Game;

public class progressGame { 

    //lastCardPlaced = arraylist.get(count);
    //if the card is slapped...


    boolean validSlap = false; 

    //                  combinations

    //sandwich
    if (lastCardPlaced.getValue().equals(arraylist.get(count-2).getValue())) {
        validSlap = true;
    }

    //top bottom
    if (lastCardPlaced.getValue().equals(arraylist.get(0).getValue())) {
        validSlap = true;
    }

    //marriage
    


    if (validSlap) {
        player.getCards();
        nextPlayer.turn();
    }

    //              face card rules

    //j
    if (lastCardPlaced.getValue().equals(11)){
        
    }

    //q
    if (lastCardPlaced.getValue().equals(12)){
        
    }

    //k
    if (lastCardPlaced.getValue().equals(13)){
        
    }

    //a
    if (lastCardPlaced.getValue().equals(1)){
        
    }
    return 0;

}
