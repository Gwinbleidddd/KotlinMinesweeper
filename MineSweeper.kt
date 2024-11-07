import kotlin.random.Random

data class Cell(var isMine: Boolean = false, var isRevealed: Boolean = false, var isFlagged: Boolean = false, var numNeighbors: Int = 0)

class MineSweeper(val width: Int, val height: Int, val numMines: Int) {
    val board = Array(height) { Array(width) { Cell() } }
    var gameOver = false
    var numRevealed = 0
    var numFlagged = 0

    fun placeMines() {
        repeat(numMines) {
            var x: Int
            var y: Int
            do {
                x = Random.nextInt(height)
                y = Random.nextInt(width)
            } while (board[x][y].isMine)
            board[x][y].isMine = true
        }
    }

    fun updateNeighbors() {
        for (x in 0 until height) {
            for (y in 0 until width) {
                if (!board[x][y].isMine) {
                    board[x][y].numNeighbors = countNeighbors(x, y)
                }
            }
        }
    }

    // determine values of cells
    private fun countNeighbors(x: Int, y: Int): Int =
        (-1..1).flatMap { dx ->
            (-1..1).map { dy ->
                val nx = x + dx
                val ny = y + dy
                if (dx == 0 && dy == 0) null else
                    if (nx in 0 until height && ny in 0 until width && board[nx][ny].isMine) 1 else null
            }
        }.count { it != null }

    // constructing the board in output
    fun printBoard() {
        // additional structure for output - coordinates
        print("    |")
        for (y in 1..width) {
            if (y in 1..9) print(" $y ") else print("$y ")
        }
        println()
        print("----|")
        for (y in 1..width) {
            print("---")
        }
        println()

        for (x in 0 until height) {
            if (x in 0..8) print("  ${x+1} | ") else print(" ${x+1} | ")
            for (y in 0 until width) {
                when {
                    board[x][y].isRevealed -> if (board[x][y].isMine) print("X  ") else print("${board[x][y].numNeighbors}  ")
                    board[x][y].isFlagged -> print("F  ")
                    else -> print(".  ")
                }
            }
            println()
        }
        numRevealed = 0
        for (i in 0 until height) {
            for (j in 0 until width) {
                if (board[i][j].isRevealed) {
                    numRevealed++
                }
            }
        }
        //       println(numRevealed)
        if (numRevealed == width * height - numMines && !gameOver) {
            gameOver = true
        }

    }

    // handling different types of moves
    fun makeMove(x: Int, y: Int, flag: Boolean = false) {
        if (gameOver) return
        if (x !in 0 until height || y !in 0 until width) {
            println("Invalid move")
            return
        }
        if (board[x][y].isRevealed) {
            println("Cell is already revealed")
            return
        }
        if (flag) {
            board[x][y].isFlagged = !board[x][y].isFlagged
        } else {
            board[x][y].isRevealed = true
            if (board[x][y].isMine) {
                gameOver = true
                println("Game over! You hit a mine.")
            } else {
                if (board[x][y].numNeighbors == 0) {
                    revealNeighbors(x, y)
                }
            }
        }
    }

    // if revealed cell has no mines nearby, open whole area
    private fun revealNeighbors(x: Int, y: Int) {
        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx == 0 && dy == 0) continue
                val nx = x + dx
                val ny = y + dy
                if (nx in 0 until height && ny in 0 until width && !board[nx][ny].isRevealed) {
                    board[nx][ny].isRevealed = true
                    if (board[nx][ny].numNeighbors == 0) {
                        revealNeighbors(nx, ny)
                    }
                }
            }
        }
    }
}

fun createBoard(width: Int, height: Int, numMines: Int): MineSweeper {
    val game = MineSweeper(width, height, numMines)
    game.placeMines()
    game.updateNeighbors()
    return game
}

fun main() {
    print("Enter board width: ")
    val width = readln()!!.toInt()
    print("Enter board height: ")
    val height = readln()!!.toInt()
    print("Enter number of mines: ")
    val numMines = readln()!!.toInt()
    val game = createBoard(width, height, numMines)

    while (!game.gameOver) {
        game.printBoard()
        if (game.gameOver && game.numRevealed == game.width * game.height - game.numMines) {
            println("Congratulations, you won!")
            break
        }
        print("Enter x, y coordinates (to reveal) (separated by space) or x, y, f (to flag) (separated by space): ")
        val input = readln()!!.split(" ")
        val x = input[0].toInt()
        val y = input[1].toInt()
        val flag = input.size > 2 && input[2] == "f"
        game.makeMove(x-1, y-1, flag)
    }
}