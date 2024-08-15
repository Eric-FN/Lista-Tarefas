package com.ericfonseca.listatarefasapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericfonseca.listatarefasapp.database.TarefaDAO
import com.ericfonseca.listatarefasapp.databinding.ActivityMainBinding
import com.ericfonseca.listatarefasapp.model.Tarefa

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)

    }

    private var listaTarefas = emptyList<Tarefa>()

    private var tarefaAdapter: TarefaAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.fabAdicionar.setOnClickListener{
            val intent = Intent(this, AdicionarTarefaActivity::class.java)
            startActivity( intent )

        }

        //RecyclerView
        tarefaAdapter = TarefaAdapter(
            {id-> confirmarExclusao(id) },
            {tarefa -> editar(tarefa)}
        )

        binding.rvTarefas.adapter = tarefaAdapter

        binding.rvTarefas.layoutManager = LinearLayoutManager(this)


    }

    private fun editar(tarefa: Tarefa) {

        val intent = Intent(this, AdicionarTarefaActivity::class.java)
        intent.putExtra("tarefa", tarefa)
        startActivity( intent )

    }

    private fun confirmarExclusao(id: Int) {

        val alertBuilder = AlertDialog.Builder(this)

        alertBuilder.setTitle("Confirmar exclusão")
        alertBuilder.setMessage("Deseja Realmente excluir a tarefa?")

        alertBuilder.setPositiveButton("sim"){_, _ ->

            val tarefaDAO = TarefaDAO(this)
            if( tarefaDAO.remover( id ) ){
                atualizarListaTarefas()
                Toast.makeText(this,
                    "Sucesso ao remover tarefa",
                    Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,
                    "Erro ao remover tarefa",
                    Toast.LENGTH_SHORT).show()
            }

        }
        alertBuilder.setNegativeButton("Não"){_, _ -> }

        alertBuilder.create().show()
    }

    private fun atualizarListaTarefas(){

            val tarefaDAO = TarefaDAO(this)
            listaTarefas = tarefaDAO.listar()
            tarefaAdapter?.adicionarLista( listaTarefas )

        }

// esse codigo está fechando o app
    override fun onStart() {
        super.onStart()
        atualizarListaTarefas()

    }
}