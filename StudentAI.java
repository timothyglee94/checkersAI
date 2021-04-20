import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.ArrayList;


//during the recursion when predicting the move of the opponent,
//ai will predict that the opponent will choose a move that makes them be the ones winning

//ai will try and choose the move that will put us in the lead 7 turns later(so it might bait the opponent and stuff idk)
//but if you have a better idea let me know idk I just thought this would be really good and not so hard to do

public class StudentAI extends AI {
    public class Node{
        // Board currentBoard; //current state of board
        //Vector<Vector<Move>> currentPossibleMoves; //current possible moves for player's turn
        int GameScore; // game score at the end of each recurrsion: [currPlayer # of pieces ] - [Opponent # pieces] --> use absolute value of GameScore to decide which move to go with
        //int depth; // the current depth, [depth  += 1] ---> once each player has made a move in the tree and resets after each iteration of currentPossibleMoves in the root node
        int depthMax; // max depth of recurssion
        int i; //keeps track of which
        int j;
        Boolean MakeMove;
        //int currplayersMove; // keeps track of whos turn
        int trueplayersmove; // Aeye true player#
    }
    public class Heuristic
    {
        int i;
        int j;
        double score;

        public Heuristic (int i, int j, double score)
        {
            this.i = i;
            this.j = j;
            this.score = score;
        }
        public double getscore()
        {
            return this.score;
        }
        public int geti()
        {
            return this.i;
        }
        public int getj()
        {
            return this.j;
        }
        public String toString()
        {
            String str = new String();
            str += "i = ";
            str += this.i;
            str += ", j = ";
            str += this.j;
            str += ", score = ";
            str += this.score;

            return str;

        }
    }
    public int nextPlayer(int i)
    {
        if(i == 1)
        {
            return 2;
        }
        else
        {
            return 1;
        }
    }
    //how to choose next move using recurrsion
    public Move chooseMove(int depth, Board currBoard, int player){
        //Move decision;
        ArrayList<Heuristic> H = new ArrayList<Heuristic>();
        int i= 0;
        int best = 0;
        boolean max = true;
        double a = Double.NEGATIVE_INFINITY;
        double b = Double.POSITIVE_INFINITY;


        Vector<Vector<Move>> currentPossibleMoves = currBoard.getAllPossibleMoves(player); //gets all moves of current player's
        Iterator<Vector<Move>> temp = currentPossibleMoves.iterator();

        while(temp.hasNext()){// iterates through all the vectors in curr.currentPossibleMoves
            Vector<Move> tmi = temp.next();
            Iterator<Move> tempMoves = tmi.iterator();
            int j= 0;
            while(tempMoves.hasNext())
            {
                Move tmj = tempMoves.next();
                Board tempB = new Board(currBoard);
                try
                {
                    tempB.makeMove(tmj, player);
                    double score = minimax(tempB, depth-1, nextPlayer(player), !max, a ,b);
                    Heuristic newH = new Heuristic(i ,j, score);
                    // System.out.println("i = " +i +" j = " +j + "move = " +tmj.toString()+ " score = "+ score);
                    H.add(newH);
                }
                catch(Exception e){}
                j++;
            }
            i++;
        }

        for(int index = 0 ; index < H.size(); index++)
        {
            //System.out.println(H.get(index).toString());
            if(H.get(index).getscore() > H.get(best).getscore())
            {
                best = index;
            }
        }
        //System.out.println("choosing move");

        //System.out.println("i = "+H.get(best).geti() + ", j = " +H.get(best).getj());
        return (currentPossibleMoves.get(H.get(best).geti())).get(H.get(best).getj());
    }

    public double minimax(Board currBoard, int depth, int player, boolean max, double a, double b)
    {

        if(depth == 0)
        {
            return gamescore(currBoard, player);
        }

        double output = 0;
        Vector<Vector<Move>> currentPossibleMoves = currBoard.getAllPossibleMoves(player);
        Iterator<Vector<Move>> temp = currentPossibleMoves.iterator();

        if(max)
        {
            output = Double.NEGATIVE_INFINITY;
            while(temp.hasNext())
            {
                Vector<Move> tmI = temp.next();
                Iterator<Move> tempMoves = tmI.iterator();

                while(tempMoves.hasNext())
                { // iterates through elements inside the vector that the first iterator gave
                    Move tmJ = tempMoves.next();
                    Board tempB = new Board(currBoard);
                    try {
                        tempB.makeMove(tmJ, player);
                    }
                    catch(Exception e){}
                    double result = minimax(tempB, depth -1, nextPlayer(player), !max, a, b);

                    output = Math.max(result, output);
                    a = Math.max(a, output);

                    if(a >= b)
                    {
                        break;
                    }
                }
            }
        }
        else
        {
            output = Double.POSITIVE_INFINITY;
            while(temp.hasNext())
            {
                Vector<Move> tmI = temp.next();
                Iterator<Move> tempMoves = tmI.iterator();

                while(tempMoves.hasNext())
                { // iterates through elements inside the vector that the first iterator gave
                    Move tmJ = tempMoves.next();
                    Board tempB = new Board(currBoard);
                    try{
                        tempB.makeMove(tmJ, player);
                    }
                    catch(Exception e){}
                    double result = minimax(tempB, depth -1, nextPlayer(player), !max, a, b);
                    output = Math.min(result, output);
                    a = Math.min(a, output);

                    if(a >= b)
                    {
                        break;
                    }
                }
            }
        }
        return output;
    }

    public int gamescore(Board curr, int player){
        int temp = 0;
        if(player == 1){
            temp = curr.blackCount - curr.whiteCount;
        }
        else{
            temp = curr.whiteCount - curr.blackCount;
        }
        return temp;
    }

    public StudentAI(int col, int row, int k) throws InvalidParameterError {
        super(col, row, k);

        this.board = new Board(col, row, k);
        this.board.initializeGame();
        this.player = 2;
    }

    public Move GetMove(Move move) throws InvalidMoveError {
        if (!move.seq.isEmpty()){
            board.makeMove(move, (player == 1) ? 2 : 1);// updates opponents turn/move and update current game state

        }
        else{


            player = 1; // should only come here if its first move of game, therfore default first move http://www.quadibloc.com/other/bo1211.htm

            //given professor's random move
            Vector<Vector<Move>> moves = board.getAllPossibleMoves(player);
            Random randGen = new Random();
            int index = randGen.nextInt(moves.size());
            int innerIndex = randGen.nextInt(moves.get(index).size());
            Move resMove = moves.get(index).get(innerIndex);
            board.makeMove(resMove, player);
            return resMove;


        }

        
        int depth = 4;
        Move temp = chooseMove(depth, this.board, this.player);
        this.board.makeMove(temp, player);
        return temp;
    }
}
