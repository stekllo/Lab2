package com.example.lab2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab2.ui.theme.Lab2Theme
import kotlinx.coroutines.launch

// Основной класс приложения
class MainActivity : ComponentActivity() {
    private val viewModel = ItemViewModel() // ViewModel хранит данные отдельно от интерфейса

    // onCreate - вызывается при запуске приложения
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { // задаём содержимое экрана
            Lab2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HousesScreen(viewModel) // вызываем экран со списком домов
                }
            }
        }
    }
}

// Главный экран - заголовок, поля ввода и список
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HousesScreen(viewModel: ItemViewModel) {
    val lazyListState = rememberLazyListState() // сохраняет позицию прокрутки
    // Column - вертикальный контейнер
    Column(Modifier.fillMaxSize()) {
        Text(
            "Дома г. Нижневартовск",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        HouseInputPart(viewModel, lazyListState) // поля ввода сверху
        HouseList(viewModel, lazyListState)       // список снизу
    }
}

// Поля ввода нового дома и кнопка добавления
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseInputPart(model: ItemViewModel, lazyListState: LazyListState) {
    // Реактивные переменные - хранят данные из полей ввода
    var street by remember { mutableStateOf("") } //mutableStateOf - для строки
    var number by remember { mutableIntStateOf(0) }//mutableIntStateOf - для чисел
    var apartments by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope() // для асинхронной прокрутки списка
    val context = LocalContext.current

    // Row - горизонтальный контейнер(располагает элементы горизонтально)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        // TextField - поле ввода, weight распределяет ширину пропорционально
        TextField(
            value = street,
            onValueChange = { street = it }, // при вводе обновляем переменную
            textStyle = TextStyle(fontSize = 16.sp),
            label = { Text("Улица") },
            modifier = Modifier.weight(2f)
        )
        TextField(
            value = number.toString(),
            onValueChange = { number = if (it != "") it.toIntOrNull() ?: 0 else 0 },
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("№") },
            modifier = Modifier.weight(1f)
        )
        TextField(
            value = apartments.toString(),
            onValueChange = { apartments = if (it != "") it.toIntOrNull() ?: 0 else 0 },
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Кв.") },
            modifier = Modifier.weight(1f)
        )
        // Кнопка добавления
        Button(
            onClick = {
                if (street.isNotEmpty()) { // проверяем что улица не пустая
                    // создаём дом и добавляем в список
                    model.addHouseToHead(House(street, number, apartments))
                    scope.launch { lazyListState.scrollToItem(0) } // прокрутка к новому элементу
                    street = ""; number = 0; apartments = 0 // очищаем поля
                } else {
                    Toast.makeText(context, "Введите улицу!", Toast.LENGTH_SHORT).show()
                    // предупреждение если пусто
                }
            },
            modifier = Modifier.weight(1f)
        ) { Text("+", fontSize = 20.sp) }
    }
}

// Прокручиваемый список домов
@Composable
fun HouseList(viewModel: ItemViewModel, lazyListState: LazyListState) {
    // LazyColumn - список, создаёт элементы только при появлении на экране
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize().background(Color.White).padding(4.dp),
        state = lazyListState
    ) {
        // items берёт список домов из ViewModel и для каждого вызывает строки списка HouseRow
        items(
            items = viewModel.houseListFlow.value,
            key = { house -> "${house.street}_${house.number}" }, // составной уникальный ключ
            itemContent = { item -> HouseRow(item) }
        )
    }
}

// Одна строка списка
@Composable
fun HouseRow(item: House) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .border(BorderStroke(2.dp, Color(0xFF4CAF50)))
            .padding(12.dp)
    ) {
        Text(item.street,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(3f)
        )
        Text("д. ${item.number}",
            fontSize = 18.sp,
            modifier = Modifier.weight(1.5f)
        )
        Text("${item.apartments} кв.",
            fontSize = 18.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.weight(1.5f)
        )
    }
}