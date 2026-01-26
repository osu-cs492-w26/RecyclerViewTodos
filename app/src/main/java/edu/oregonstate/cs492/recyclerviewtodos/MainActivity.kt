package edu.oregonstate.cs492.recyclerviewtodos

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val todoEntryET: EditText = findViewById(R.id.et_todo_entry)
        val addTodoBtn: Button = findViewById(R.id.btn_add_todo)
        val todoListRV: RecyclerView = findViewById(R.id.rv_todo_list)

        todoListRV.layoutManager = LinearLayoutManager(this)
        todoListRV.setHasFixedSize(true)

        val adapter = TodoAdapter()
        todoListRV.adapter = adapter

        addTodoBtn.setOnClickListener {
            val newTodo = todoEntryET.text.toString()
            if (!TextUtils.isEmpty(newTodo)) {
                adapter.addTodo(Todo(newTodo))
                todoListRV.scrollToPosition(0)
                todoEntryET.setText("")
            }
        }

        val coordinatorLayout: CoordinatorLayout = findViewById(R.id.main)

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val deletedTodo = adapter.deleteTodoAt(position)

                val snackbar = Snackbar.make(
                    coordinatorLayout,
                    "Deleted: ${deletedTodo.text}",
                    Snackbar.LENGTH_LONG
                )
                snackbar.setAction("UNDO") {
                    adapter.addTodo(deletedTodo, position)
                }
                snackbar.show()
            }
        }

        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(todoListRV)
    }
}