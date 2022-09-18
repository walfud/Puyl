package com.walfud.puyl

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.walfud.extention.toSimpleString
import java.time.LocalDate
import java.time.LocalTime


const val ROUTINE_TASK = "task"

@Composable
fun TaskPage(
    taskVM: TaskViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // header
        item {
            TaskHeader()
        }

        // tasks
        items(taskVM.taskDatas) { taskData ->
            val taskContext = TaskContext(taskData.name, taskVM.beginDate, taskVM.endDate)
            TaskItem(taskContext, taskVM.testData())
        }
    }
}

@Composable
fun TaskHeader() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // name
        Text(
            "任务名",
            modifier = Modifier.width(100.dp),
            textAlign = TextAlign.Center,
        )
        Box(
            modifier = Modifier.width(10.dp).height(8.dp).align(Alignment.CenterVertically).background(Color.Black)
        )
        Text(
            "职能",
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            "对接人",
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            "排期",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun foo() {
    TaskHeader()
}

/**
 * |----------------------------------------------- TaskData -------------------------------------------------|
 *           |---------------------------------------------- ExecutorData ------------------------------------|
 *                                |--------------------------------- ActivityData ----------------------------|
 * | TaskDesc |   name  |   who   |                              activity                                     |
 * |==========================================================================================================|
 * |          |    PM   | walfud  |   EVALUATE@("2022-09-04", "2022-09-04"), DEV@("2022-09-04", "2022-11-11") |
 * |  Task A  |   iOS   | tony    |   DEV@("2022-09-08", "2022-12-04")                                        |
 * |          |  Tester | frank   |   TEST@("2022-12-08", "2022-12-31")                                       |
 * |==========================================================================================================|
 */
@Composable
fun TaskItem(taskContext: TaskContext, taskData: TaskData) {
    Row(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black)
    ) {
        // name
        Text(
            taskData.name,
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center,
        )

        // executors
        LazyColumn {
            items(taskData.executors) { execData ->
                Executor(taskContext, execData)
            }
        }
    }
}

@Composable
fun Executor(taskContext: TaskContext, executorData: ExecutorData) {
    // name
    Text(
        executorData.name,
        modifier = Modifier.width(80.dp),
    )

    // who
    Text(
        executorData.who,
        modifier = Modifier.width(80.dp),
    )

    // activity
    val dayCount = (taskContext.endDate.toEpochDay() - taskContext.beginDate.toEpochDay() + 1).toInt()
    if (dayCount <= 0) {
        return
    }
    val actArr = Array(dayCount) { index ->
        val currDate = taskContext.beginDate.plusDays(index.toLong())
        val act = executorData.acts.find { actData ->
            val actBeginDate = actData.beginDate
            val actEndDate = actData.endDate
            if (actBeginDate == null || actEndDate == null) {
                return@find false
            }
            if (actEndDate.isBefore(actBeginDate)) {
                Logger.e("...")
                return@find false
            }
            return@find actBeginDate <= currDate && currDate <= actEndDate
        }

        return@Array act?.type ?: ActivityType.DEFAULT
    }
    LazyRow {
        items(actArr) { actData ->
            ActivityItem(actData)
        }
    }
}

@Composable
fun ActivityItem(actType: ActivityType) {
    val (borderColor, bgColor) = when (actType) {
        ActivityType.DEFAULT -> listOf(Color.LightGray, Color.Black)
        ActivityType.PRE_EVALUATE -> listOf(Color.LightGray, Color.Black)
        ActivityType.EVALUATE -> listOf(Color.LightGray, Color.Black)
        ActivityType.DEV -> listOf(Color.LightGray, Color.Black)
        ActivityType.JOINT_DEBUG -> listOf(Color.LightGray, Color.Black)
        ActivityType.TEST -> listOf(Color.LightGray, Color.Black)
        ActivityType.PUBLISH -> listOf(Color.LightGray, Color.Black)
        else -> listOf(Color.LightGray, Color.Black)
    }

    Box(
        modifier = Modifier.size(24.dp, 16.dp)
            .border(1.dp, borderColor)
            .background(bgColor),
    )
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
    DEFAULT,        // 空闲

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

data class TaskContext(
    val taskName: String,
    val beginDate: LocalDate,
    val endDate: LocalDate,
)

class TaskViewModel(navController: NavController) : BaseViewModel(navController) {
    var beginDate: LocalDate by mutableStateOf(LocalDate.now().minusDays(5))
    var endDate: LocalDate by mutableStateOf(LocalDate.now().plusDays(13))
    val taskDatas: List<TaskData> = mutableStateListOf()

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