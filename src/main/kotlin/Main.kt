package org.example

sealed class Figure(property: Double) {
    init {
        require(property > 0 && !property.isNaN()) { "Property must be greater than zero and not NaN" }
    }
}

data class Circle(private val radius: Double) : Figure(radius) {
    fun area() = Math.PI * radius * radius
    fun perimeter() = 2 * Math.PI * radius
}

data class Square(private val side: Double) : Figure(side) {
    fun area() = side * side
    fun perimeter() = 4 * side
}

interface ConsoleService {
    fun work()
}

interface FigureService {
    fun addSquare(property: Double)
    fun addCircle(property: Double)
    fun getPerimeter(): Double
    fun getArea(): Double
}

class BadPropertyException(value: Double) : Exception("Введено неверное значение параметра property: $value")
class WrongOperationTypeException(value: String) : Exception("Введен неизвестный тип операции: $value")

enum class Operation {
    INSERT, GET_AREA, GET_PERIMETER, EXIT
}

class FigureServiceImpl : FigureService {
    private val figures = mutableListOf<Figure>()

    override fun addSquare(property: Double) {
        if (property <= 0 || property.isNaN()) throw BadPropertyException(property)
        figures.add(Square(property))
        println(Square(property))
    }

    override fun addCircle(property: Double) {
        if (property <= 0 || property.isNaN()) throw BadPropertyException(property)
        figures.add(Circle(property))
        println(Circle(property))
    }

    override fun getPerimeter(): Double {
        return figures.sumOf {
            when (it) {
                is Circle -> it.perimeter()
                is Square -> it.perimeter()
            }
        }
    }

    override fun getArea(): Double {
        return figures.sumOf {
            when (it) {
                is Circle -> it.area()
                is Square -> it.area()
            }
        }
    }
}

class ConsoleServiceImpl(private val figureService: FigureService) : ConsoleService {
    override fun work() {
        while (true) {
            println("Введите тип операции, которую хотите исполнить:\n1) добавить фигуру\n2) получить площадь всех фигур\n3) получить периметр всех фигур\n4) завершить выполнение")
            try {
                val operation = getOperation(readln())
                when (operation) {
                    Operation.INSERT -> addFigure()
                    Operation.GET_AREA -> getArea()
                    Operation.GET_PERIMETER -> getPerimeter()
                    Operation.EXIT -> break
                }
            } catch (e: BadPropertyException) {
                println(e.message)
            } catch (e: WrongOperationTypeException) {
                println(e.message)
            }
        }
    }

    private fun addFigure() {
        println("Введите тип фигуры (1 - Круг, 2 - Квадрат):")
        val type = readln().toIntOrNull()
        println("Введите значение параметра property:")
        val property = readln().toDoubleOrNull() ?: throw BadPropertyException(Double.NaN)
        when (type) {
            1 -> figureService.addCircle(property)
            2 -> figureService.addSquare(property)
            else -> throw WrongOperationTypeException(type.toString())
        }
    }

    private fun getArea() {
        println("Общая площадь всех фигур: ${figureService.getArea()}")
    }

    private fun getPerimeter() {
        println("Общий периметр всех фигур: ${figureService.getPerimeter()}")
    }

    private fun getOperation(input: String): Operation {
        return when (input) {
            "1" -> Operation.INSERT
            "2" -> Operation.GET_AREA
            "3" -> Operation.GET_PERIMETER
            "4" -> Operation.EXIT
            else -> throw WrongOperationTypeException(input)
        }
    }
}

fun main() {
    val figureService = FigureServiceImpl()
    val consoleService = ConsoleServiceImpl(figureService)
    consoleService.work()
}

