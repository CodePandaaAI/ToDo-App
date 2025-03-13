package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.todoapp.ui.theme.ToDoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                Scaffold { innerPadding ->
                    TaskListScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class Task(val name: String, var isPending: Boolean = true)

class TaskManager {
    private val _tasksList = mutableStateListOf<Task>() // Private backing list
    val tasksList: List<Task> get() = _tasksList.toList() // Public read-only view

    fun addTask(name: String) {
        if (name.isNotBlank()) {
            _tasksList.add(Task(name.trim()))
        }
    }

    fun markTaskAsDone(task: Task) {
        val index = _tasksList.indexOf(task)
        if (index != -1) {
            _tasksList[index] = task.copy(isPending = false)
        }
    }
    fun markTaskAsPending(task: Task) {
        val index = _tasksList.indexOf(task)
        if (index != -1) {
            _tasksList[index] = task.copy(isPending = true)
        }
    }

    fun clearFinishedTasks() {
        _tasksList.removeAll { !it.isPending }
    }
}

@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    taskManager: TaskManager = remember { TaskManager() }
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TaskInput(taskManager) // Task Add UI

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(taskManager.tasksList, key = { it.name }) { task ->
                TaskItem(task, taskManager)
            }
        }

        ClearFinishedTasksButton(taskManager)
    }
}

@Composable
fun ClearFinishedTasksButton(taskManager: TaskManager) {
    Button(
        onClick = { taskManager.clearFinishedTasks() },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Text("Clear Finished Tasks", color = Color.White)
    }
}

@Composable
fun TaskInput(taskManager: TaskManager) {
    var taskName by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            shape = CircleShape,
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Enter Task") },
            modifier = Modifier.weight(1f)
        )
        Button(onClick = {
            taskManager.addTask(taskName)
            taskName = "" // Reset input field
        }, enabled = taskName.isNotBlank()) {
            Text("Add")
        }
    }
}

@Composable
fun TaskItem(task: Task, taskManager: TaskManager) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Increased horizontal padding for better spacing and consistency.
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Reduced elevation for a subtle effect.
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Added padding to the Row for content spacing.
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Use spacedBy for consistent spacing.
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = !task.isPending,
                onCheckedChange = { isChecked ->
                    if (isChecked)
                        taskManager.markTaskAsDone(task)
                    else
                        taskManager.markTaskAsPending(task)
                }
            )

            Column(modifier = Modifier.weight(1f)){
                Text(
                    text = task.name,
                    fontWeight = FontWeight.Medium // Added a font weight for the task name
                )
                Text(text = if (task.isPending) "Pending" else "Done")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskListPreview() {
    ToDoAppTheme {
        Scaffold { innerPadding ->
            TaskListScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}
