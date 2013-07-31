About
---------
This is a human-computer interactive checkers game. User can choose to play the game in different levels. 

Game Rules
---------
1. **Choose roles.**
The checkerboard consists of 6 x 6 alternating light and dark squares (see the following picture). Each player starts out with 6 pieces. One player has white pieces and the other has black pieces. The player with the black pieces always moves first. User can choose to have the black pieces or the white pieces.

    ![board](https://raw.github.com/ceciliazhou/russian_checkers/master/board.png)

2. **Move.**
Each player takes turn to move his/her pieces. There are two types of moves: regular moves and jumps. In a regular move, a piece can move forward (and only forward) one square diagonally to an adjacent square that is empty. In a jump, a piece can jump over and capture an opponent's piece. A piece can jump forward or backward diagonally to a position that is empty. A move can also consist of consecutive jumps, as long as each jump is over an opponent's piece and land over an empty position.  In addition, every opportunity to jump must be taken. 

3. **Win or lose.**
A player wins when he/she captures all of the other player's pieces, or when the other player has no legal move that he/she can make. 

Try it
----------
1. Compile the source code:

        javac *.java

2. Run the game:

        java CheckersGame