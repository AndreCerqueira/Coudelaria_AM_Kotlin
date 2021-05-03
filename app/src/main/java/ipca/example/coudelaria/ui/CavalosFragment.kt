package ipca.example.coudelaria.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ipca.example.coudelaria.MainActivity
import ipca.example.coudelaria.R
import ipca.example.coudelaria.models.Cavalo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject


class CavalosFragment : Fragment() {

    // Global Variables
    lateinit var listView : ListView
    lateinit var adapter : CavalosAdapter
    var cavalos : MutableList<Cavalo> = arrayListOf()

    /*
        Fragment creation method, returns the fragment created
        @inflater = xml layout that is being instantiated
        @container = nao sei explicar ao certo
        @savedInstanceState = nao é importante saber. apenas tem de ter.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Create the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_cavalos, container, false)

        // Set data
        listView = rootView.findViewById(R.id.listViewCavalos)
        adapter = CavalosAdapter()
        listView.adapter = adapter

        return rootView
    }


    /*
        After fragment creation
        @view = fragment
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Sem isto o programa continua a funcionar. nao sei para que serve
        super.onViewCreated(view, savedInstanceState)

        // Start Corroutine
        GlobalScope.launch(Dispatchers.IO) {

            // OkHttp possibilita o envio de pedidos http e a leitura das respostas
            val client = OkHttpClient()

            // criação do pedido http á api do .NET
            val request = Request.Builder().url("http://" + MainActivity.IP + ":5000/api/Cavalos").build()

            // fazer a chamada com o pedido http e analisar a resposta
            client.newCall(request).execute().use { response ->

                // resposta do pedido http retornada em string
                val str : String = response.body!!.string()

                // converter a str em um array de json, e esse json é de cavalos
                val jsonArrayCavalos = JSONArray(str)

                // add the horses to the list
                for (index in 0 until jsonArrayCavalos.length()) {
                    val jsonArticle = jsonArrayCavalos.get(index) as JSONObject
                    val cavalo = Cavalo.fromJson(jsonArticle)
                    cavalos.add(cavalo)
                }

                // Refresh the list adapter
                GlobalScope.launch (Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }

            }

        }

    }

    inner class CavalosAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return cavalos.size
        }

        override fun getItem(position: Int): Any {
            return cavalos[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_cavalo, parent, false)

            // Variables
            val textViewCod = rowView.findViewById<TextView>(R.id.textViewCodCavalo)
            val textViewNomeCavalo = rowView.findViewById<TextView>(R.id.textViewNomeCavalo)

            // Set data
            textViewCod.text = cavalos[position].codCavalo.toString()
            textViewNomeCavalo.text = cavalos[position].nomeCavalo

            return rowView
        }
    }

}