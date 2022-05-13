class Chess {
    // у шашки есть цвет
    private val white = 1
    private val black = 0

    //функция получения ячейки
    fun getCell(): Set<Double> {
        return getCoordinates().keys
    }

    //получение координат
    fun getXY(): Collection<Pair<Double, Double>> {
        return getCoordinates().values
    }

    //функции: если ячейка пустая и соседняя возможен ход;
    //если на стыке шашки 0 и 1 (разные цвета), то можно бить;

}