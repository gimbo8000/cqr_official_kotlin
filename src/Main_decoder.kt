import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

const val NULL_BLOCK_CHAR = '\u001A'

fun getChunkFromColor(color: IntArray): String {
    val r = color[0]
    val g = color[1]
    val b = color[2]
    return r.toChar().toString() + g.toChar() + b.toChar()
}

fun decodeSnakingQrGrid(imagePath: String): String {
    val img: BufferedImage = ImageIO.read(File(imagePath))
    val imgSize = img.width
    val cellSize = 50
    val gridSize = imgSize / cellSize

    // Generate snaking coordinates
    val coordinates = mutableListOf<Pair<Int, Int>>()
    for (row in gridSize - 1 downTo 0) {
        if ((gridSize - 1 - row) % 2 == 0) {
            for (col in gridSize - 1 downTo 0) {
                coordinates.add(row to col)
            }
        } else {
            for (col in 0 until gridSize) {
                coordinates.add(row to col)
            }
        }
    }

    // Corner positions to skip
    val corners = setOf(0 to 0, 0 to gridSize - 1, gridSize - 1 to 0)
    var decodedUrl = ""

    for ((row, col) in coordinates) {
        if (row to col !in corners) {
            val x = col * cellSize + cellSize / 2
            val y = row * cellSize + cellSize / 2
            val color = img.getRGB(x, y)
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF
            val chunk = getChunkFromColor(intArrayOf(r, g, b))

            // Stop processing if NULL_BLOCK is encountered
            for (char in chunk) {
                if (char == NULL_BLOCK_CHAR) {
                    return decodedUrl
                }
                decodedUrl += char
            }
        }
    }

    return decodedUrl
}

fun main() {
    val imagePath = "null_block_qr_grid.png"
    val decodedUrl = decodeSnakingQrGrid(imagePath)
    println("Decoded URL: $decodedUrl")
}
