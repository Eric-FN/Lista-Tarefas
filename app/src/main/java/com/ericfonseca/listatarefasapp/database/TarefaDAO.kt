package com.ericfonseca.listatarefasapp.database

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.ericfonseca.listatarefasapp.model.Tarefa

class TarefaDAO(context: Context): ITarefaDAO {

    private val escrita = DatabaseHelper(context).writableDatabase
    private val leitura = DatabaseHelper(context).readableDatabase


    override fun salvar(tarefa: Tarefa): Boolean {

        val conteudos = ContentValues()
        conteudos.put("${DatabaseHelper.COLUNA_DESCRICAO}", tarefa.descricao)

        try{
            escrita.insert(
                DatabaseHelper.NOME_TABELA_TAREFAS,
                null,
                conteudos

            )
            Log.i("info_db", "Sucesso ao salvar tarefa")
        }catch (e: Exception){
            e.printStackTrace()
            Log.i("info_db", "Erro ao salvar tarefa")
            return false
        }
        return true
    }

    override fun atualizar(tarefa: Tarefa): Boolean {
        val args = arrayOf( tarefa.idTarefa.toString() )
        val conteudo = ContentValues()
        conteudo.put("${DatabaseHelper.COLUNA_DESCRICAO}", tarefa.descricao)
        try{
            escrita.update(
                DatabaseHelper.NOME_TABELA_TAREFAS,
                conteudo,
                "${DatabaseHelper.COLUNA_ID_TAREFA} = ?",
                args
            )

            Log.i("info_db", "Sucesso ao atualizar tarefa")
        }catch (e: Exception){
            e.printStackTrace()
            Log.i("info_db", "Erro ao atualizar tarefa")
            return false
        }
        return true
    }

    override fun remover(idTarefa: Int): Boolean {

        val args = arrayOf( idTarefa.toString() )
        try{
            escrita.delete(
                DatabaseHelper.NOME_TABELA_TAREFAS,
                "${DatabaseHelper.COLUNA_ID_TAREFA} = ?",
                args
            )

            Log.i("info_db", "Sucesso ao remover tarefa")
        }catch (e: Exception){
            e.printStackTrace()
            Log.i("info_db", "Erro ao remover tarefa")
            return false
        }
        return true

    }

    //provavelmente esse também está errado
    override fun listar(): List<Tarefa> {

        val listaTarefas = mutableListOf<Tarefa>()

        val sql = "SELECT ${DatabaseHelper.COLUNA_ID_TAREFA}, " +
                "${DatabaseHelper.COLUNA_DESCRICAO}," +
                "strftime('%d/%m/%Y %H:%M', ${DatabaseHelper.COLUNA_DATA_CADASTRO}) data_cadastro " +
                "FROM ${DatabaseHelper.NOME_TABELA_TAREFAS} "

        val cursor= leitura.rawQuery(sql, null)

        val indiceId = cursor.getColumnIndex( DatabaseHelper.COLUNA_ID_TAREFA )
        val indiceDescricao = cursor.getColumnIndex( DatabaseHelper.COLUNA_DESCRICAO )
        val indiceData = cursor.getColumnIndex( DatabaseHelper.COLUNA_DATA_CADASTRO )

        while (cursor.moveToNext()){
            val idTarefa = cursor.getInt(indiceId)
            val descricao = cursor.getString(indiceDescricao)
            val idData = cursor.getString(indiceData)

            listaTarefas.add(
                Tarefa(idTarefa, descricao, idData)
            )

        }
        return listaTarefas

    }
}