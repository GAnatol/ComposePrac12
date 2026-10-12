package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Column() {
                    MainApp()
                }

            }
        }
    }
}

data class Task(val id: Int, val title: String, val isCompleted: Boolean)


@Composable
fun MainApp() {
    var screen by rememberSaveable { mutableStateOf(0) }

    val tasks = rememberSaveable {
        mutableStateListOf(
            Task(1, "Прочитать главу по Compose", true),
            Task(2, "Сделать практическую 12", false),
            Task(3, "Проверить домашку", false)
        )
    }
    var taskInput by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf("Все") }

    when (screen) {
        0 -> MainScreen(
            onOpenTasks = { screen = 2 },
            onOpenStats = { screen = 1 }
        )
        1 -> StatsScreen(
            tasks = tasks,
            onBack = { screen = 0 }
        )
        2 -> TasksScreen(
            tasks = tasks,
            taskInput = taskInput,
            onTaskInputChange = { taskInput = it },
            selectedFilter = selectedFilter,
            onFilterChange = { selectedFilter = it },
            onAddTask = {
                if (taskInput.isNotBlank()) {
                    tasks.add(Task(id = (tasks.maxOfOrNull { it.id } ?: 0) + 1, title = taskInput, isCompleted = false))
                    taskInput = ""
                }
            },
            onToggleTask = { task ->
                val index = tasks.indexOfFirst { it.id == task.id }
                if (index != -1) {
                    tasks[index] = task.copy(isCompleted = !task.isCompleted)
                }
            },
            onDeleteTask = { task ->
                tasks.remove(task)
            },
            onBack = { screen = 0 }
        )
    }
}

@Composable
fun MainScreen(onOpenTasks: () -> Unit, onOpenStats: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.magnolia))
            .padding(13.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Surface(
            modifier = Modifier.size(190.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text(text = "Ваша иллюстрация", color = Color.Gray, fontSize = 14.sp) }
        }



        Text(
            "Orginize Your Day",
            modifier = Modifier
                .padding(top = 40.dp),
            fontSize = 26.sp,
            color = colorResource(R.color.purple),
            fontWeight = FontWeight.Bold
        )
        Text(
            "Планируйте задачи, отмечайте выполненное и следите за прогрессом",
            modifier = Modifier
                .padding(top = 10.dp),
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onOpenTasks,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.purple)
                )
            ) {
                Text("Мои задачи")
            }
            OutlinedButton(
                onClick = onOpenStats,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, colorResource(R.color.purple)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorResource(R.color.purple)
                )
            ) {
                Text("Статистика")
            }
        }
    }
}


@Composable
fun StatsScreen(tasks: List<Task>, onBack: () -> Unit) {
    val completedTasks: Int = (tasks.filter { it.isCompleted }).size
    val allTasks: Int = tasks.size
    val uncompletedTasks: Int = allTasks - completedTasks
    val progressFloat: Float = if (allTasks > 0) completedTasks.toFloat() / allTasks else 0f

    val progressPercent: Int = (progressFloat * 100).toInt()

    Column (
        Modifier
            .background(colorResource(R.color.magnolia))
            .padding(20.dp)
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(colorResource(R.color.magnolia))
                .padding(top = 30.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(colorResource(R.color.magnolia))
                    .padding(end = 10.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = colorResource(R.color.kolokolchik)
                )
            }

            Text("Статистика", fontWeight = FontWeight.Bold, fontSize = 25.sp)
        }
        Spacer(Modifier.height(10.dp))



        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.magnolia))

        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(240.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Прогресс", color = Color.Gray, fontSize = 14.sp)
                        Spacer(Modifier.height(20.dp))

                        Text(
                            "$progressPercent%",
                            fontSize = 46.sp,
                            color = colorResource(R.color.purple),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(20.dp))

                        LinearProgressIndicator(
                            progress = { progressFloat },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = colorResource(R.color.purple),
                            trackColor = Color.LightGray.copy(alpha = 0.3f),
                            gapSize = 0.dp
                        )
                        Spacer(Modifier.height(16.dp))

                        Text("Выполнено $completedTasks из $allTasks задач", fontSize = 14.sp)
                    }
                }

            }
            Spacer(Modifier.height(16.dp))



            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$uncompletedTasks",
                                fontSize = 26.sp,
                                color = colorResource(R.color.purple),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Активные", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }

                Surface(
                    Modifier.weight(1f).height(100.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$completedTasks",
                                fontSize = 26.sp,
                                color = colorResource(R.color.purple),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(10.dp))
                            Text("Готово", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TasksScreen(
    tasks: List<Task>,
    taskInput: String,
    onTaskInputChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onAddTask: () -> Unit,
    onToggleTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onBack: () -> Unit
) {
    val filteredTasks = when (selectedFilter) {
        "Активные" -> tasks.filter { !it.isCompleted }
        "Выполненные" -> tasks.filter { it.isCompleted }
        else -> tasks
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.magnolia))
            .padding(13.dp),
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(colorResource(R.color.magnolia))
                .padding(top = 30.dp)
        ) {
            IconButton(
                onClick = onBack ,
                modifier = Modifier
                    .background(colorResource(R.color.magnolia))
                    .padding(end = 10.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = colorResource(R.color.kolokolchik)
                )
            }
            Column {
            Text("Мои задачи", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Text(
                "Выполнено ${(tasks.filter { it.isCompleted }).size} из ${tasks.size}",
                color = Color.Gray,
                fontSize = 14.sp
            )
            }
        }
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedTextField(
                value = taskInput,
                onValueChange = onTaskInputChange,
                placeholder = { Text("Новая задача...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(size = 12.dp),
                singleLine = true
            )

            Button(
                onClick = onAddTask,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.purple)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("Все", "Активные", "Выполненные").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    colors =  FilterChipDefaults.filterChipColors(
                        colorResource(R.color.magnolia),
                        selectedContainerColor = Color(0xFFe7ddf8)
                    ),
                    onClick = { onFilterChange(filter) },
                    label = { Text(filter) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTasks, key = { it.id }) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { onToggleTask(task) },
                                colors = CheckboxDefaults.colors(
                                    colorResource(R.color.purple)
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = task.title,
                                fontSize = 14.sp,
                                color = if (task.isCompleted) Color.Gray else Color.Black
                            )
                        }
                        IconButton(onClick = { onDeleteTask(task) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Удалить",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}



/*
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
    }
}
*/
