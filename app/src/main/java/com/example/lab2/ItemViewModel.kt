package com.example.lab2

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// data class - класс данных для хранения информации о доме
data class House(val street: String, val number: Int, val apartments: Int)

// ViewModel - хранит данные отдельно от интерфейса, не теряет их при перерисовке
class ItemViewModel : ViewModel() {

    // mutableStateListOf - реактивный список, UI обновляется при изменении
    private var houseList = mutableStateListOf(
        House("ул. Ленина", 15, 80),
        House("ул. Мира", 22, 60),
        House("ул. 60 лет Октября", 48, 120),
        House("ул. Интернациональная", 37, 90),
        House("ул. Дзержинского", 10, 45),
        House("ул. Омская", 28, 70),
        House("ул. Чапаева", 19, 55),
        House("пр. Победы", 6, 100)
    )

    // StateFlow - поток данных, интерфейс отслеживает изменения через него
    private val _houseListFlow = MutableStateFlow(houseList)
    val houseListFlow: StateFlow<List<House>> get() = _houseListFlow

    // Добавление дома в начало списка
    fun addHouseToHead(house: House) {
        houseList.add(0, house)
    }
}