import java.io.File
import java.io.FileNotFoundException
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

enum class Order {
    NATURAL,
    SHORT_TO_LONG,
    LONG_TO_SHORT
}

data class Item(
        val id: Int,
        val length: Int,
        var quantity: Int,
        val weight: Double
)

data class Pack(
        val totalPieces: Int,
        val totalWeight: Double
)

tailrec fun List<Item>.pack(
        position: Int,
        maxPieces: Int,
        maxWeight: Double,
        pieces: Int,
        weight: Double,
        length: Int
): Int {
    val item = this[position]

    val remainingPieces = maxPieces - pieces;
    val remainingWeight = maxWeight - weight;

    val piecesToAdd = min(min(remainingPieces, item.quantity), floor(remainingWeight / item.weight).toInt())

    val totalPieces = pieces + piecesToAdd
    val totalWeight = weight + item.weight * piecesToAdd
    val currentLength = max(length, item.length)

    item.quantity -= piecesToAdd

    println("${item.id},${item.length},${piecesToAdd},${item.weight}")

    return when {
        piecesToAdd == 0 || item.quantity > 0 || position == lastIndex -> {
            println("Pack Length: $currentLength, Pack Weight: ${String.format("%.2f", totalWeight)}")
            when {
                item.quantity > 0 -> position
                else -> position + 1
            }
        }
        else -> pack(position + 1, maxPieces, maxWeight, totalPieces, totalWeight, currentLength)
    }
}

fun List<Item>.pack(maxPieces: Int, maxWeight: Double) {
    var position = 0;
    var pack = 0;
    do {
        println("Pack Number: ${++pack}")
        position = pack(position, maxPieces, maxWeight, 0, 0.0, 0)
    } while (position < size)
}

fun main(args: Array<String>) {
    try {
        val input = File(args.first()).readLines()
        val limitations = input.first().split(',')

        limitations.forEach {
            println(it)
        }

        val order = Order.valueOf(limitations[0])
        val maxPieces = limitations[1].toInt()
        val maxWeight = limitations[2].toDouble()

        input.drop(1)
                .map {
                    val params = it.split(',')
                    Item(params[0].toInt(), params[1].toInt(), params[2].toInt(), params[3].toDouble())
                }.let { items ->
                    when (order) {
                        Order.NATURAL -> items
                        Order.LONG_TO_SHORT -> items.sortedByDescending { it.length }
                        Order.SHORT_TO_LONG -> items.sortedBy { it.length }
                    }
                }
                .pack(maxPieces, maxWeight)

    } catch (e: FileNotFoundException) {
        println("File not found.")
    } catch (e: NoSuchElementException) {
        println("Please enter the path to the input file.")
    }
}
