package com.walfud.puyl

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.walfud.extention.toSimpleString
import java.time.LocalDate
import java.time.LocalTime
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


const val ROUTINE_TASK = "task"

@Composable
fun TaskPage(
    taskVM: TaskViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            TaskItem(taskVM.beginDate, taskVM.endDate, taskVM.testData())
        }
    }
}

/**
 * |----------------------------------------------- TaskData -------------------------------------------------|
 *           |---------------------------------------------- ExecutorData ------------------------------------|
 *                              |----------------------------------- ActivityData ----------------------------|
 * | TaskDesc |   name  |   who   |                           activity                                        |
 * |==========================================================================================================|
 * |          |    PM   | walfud  |   EVALUATE@("2022-09-04", "2022-09-04"), DEV@("2022-09-04", "2022-11-11") |
 * |  Task A  |   iOS   | tony    |   DEV@("2022-09-08", "2022-12-04")                                        |
 * |          |  Tester | frank   |   TEST@("2022-12-08", "2022-12-31")                                       |
 * |==========================================================================================================|
 */
@Composable
fun TaskItem(beginDate: LocalDate, endDate: LocalDate, taskData: TaskData) {
    Row(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black)
    ) {
        // task
        Text(
            taskData.name,
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center,
        )

        // name & who
        Column {
            taskData.executors.forEach { executorData ->
                Text(
                    executorData.name,
                    modifier = Modifier.width(80.dp),
                )
            }
        }
        Column {
            taskData.executors.forEach { executorData ->
                Text(
                    executorData.who,
                    modifier = Modifier.width(80.dp),
                )
            }
        }

        // activity
        val dateCount = (endDate.toEpochDay() - beginDate.toEpochDay() + 1).toInt()
        if (dateCount <= 0) {
            return
        }
        val matrix = Array<Array<FlatActData?>>(taskData.executors.size) { Array(dateCount) { null } }
        taskData.executors.forEachIndexed { execDataIndex, executorData ->
            executorData.acts.forEach { actData ->
                if (actData.beginDate == null || actData.endDate == null) {
                    Logger.e("...")
                    return@forEach
                }
                val actBeginDate = actData.beginDate
                val actEndDate = actData.endDate
                if (actEndDate.isBefore(actBeginDate)) {
                    Logger.e("...")
                    return@forEach
                }

                var currDate = LocalDate.from(actBeginDate)
                var i = 0L
                while (true) {
                    currDate = currDate.plusDays(i)
                    if (currDate.isAfter(actEndDate) || currDate.isAfter(endDate)) {
                        break
                    }

                    val pos = (currDate.toEpochDay() - endDate.toEpochDay()).toInt()
                    matrix[execDataIndex][pos] = FlatActData(
                        taskData.name,
                        executorData.name,
                        executorData.who,
                        actData.type,
                        currDate,
                    )

                    i++
                }
            }
        }

        LazyRow {

        }
    }
}

@Composable
fun ActivityItem() {

}

data class TaskData(
    val name: String,
    val executors: List<ExecutorData>,
)

data class ExecutorData(
    val name: String,
    val who: String,
    val acts: List<ActivityData>,
)

enum class ActivityType {
    DEFAULT,        // reserved

    PRE_EVALUATE,   // 预评
    EVALUATE,       // 详评
    DEV,            // 开发
    JOINT_DEBUG,    // 联调
    TEST,           // 测试
    PUBLISH,        // 上线
}

data class ActivityData(
    val type: ActivityType,
    val beginDate: LocalDate?,     // [2022-09-04
    val endDate: LocalDate?,       // 2022-09-05]
)

data class FlatActData(
    val taskName: String,
    val execName: String,
    val who: String,
    val type: ActivityType,
    val date: LocalDate,
)

class TaskViewModel(navController: NavController) : BaseViewModel(navController) {
    var beginDate: LocalDate by mutableStateOf(LocalDate.now().minusDays(5))
    var endDate: LocalDate by mutableStateOf(LocalDate.now().plusDays(13))

    fun testData(): TaskData {
        val date = LocalDate.now()
        val timeStr = LocalTime.now().toSimpleString()
        val taskName = "test task: $timeStr"
        return TaskData(
            taskName,
            listOf(
                ExecutorData(
                    "test executor: $timeStr",
                    "walfud",
                    listOf(
                        ActivityData(
                            ActivityType.PRE_EVALUATE,
                            date,
                            date,
                        ),
                    )
                )
            ),
        )
    }
}