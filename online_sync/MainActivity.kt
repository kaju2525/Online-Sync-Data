package karun.com.online_sync

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import karun.com.googlemapdemo.MyAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        val url = "http://karunkumar.in/offiline/offline.php"
        val NAME_SYNCED_WITH_SERVER = 1
        val NAME_NOT_SYNCED_WITH_SERVER = 0
        val DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved"
    }



    private var list: ArrayList<Model>? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    private var myAdapter: MyAdapter? = null
    private var db: DatabaseHelper? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(NetworkStateChecker(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        recyclearView.layoutManager=LinearLayoutManager(this,LinearLayout.VERTICAL,false)
        db = DatabaseHelper(this)
        list = ArrayList<Model>()



        buttonSave!!.setOnClickListener(this)
        loadNames()

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                loadNames()
            }
        }

        registerReceiver(broadcastReceiver, IntentFilter(DATA_SAVED_BROADCAST))
    }
    private fun loadNames() {
        list!!.clear()
        val cursor = db!!.names
        if (cursor.moveToFirst()) {
            do {
                val name = Model(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                )
                list!!.add(name)
            } while (cursor.moveToNext())
        }

        myAdapter = MyAdapter(list!!)
        recyclearView!!.adapter = myAdapter
    }

    private fun refreshList() {
        myAdapter!!.notifyDataSetChanged()
    }

    private fun saveNameToServer() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Saving Model...")
        progressDialog.show()

        val name = editTextName!!.text.toString().trim { it <= ' ' }


        val stringRequest = object : StringRequest(com.android.volley.Request.Method.POST, url,
                com.android.volley.Response.Listener { response ->
                    progressDialog.dismiss()
                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            //if there is a success
                            //storing the name to sqlite with status synced
                            saveNameToLocalStorage(name, NAME_SYNCED_WITH_SERVER)
                        } else {
                            //if there is some error
                            //saving the name to sqlite with status unsynced
                            saveNameToLocalStorage(name, NAME_NOT_SYNCED_WITH_SERVER)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener {
                    progressDialog.dismiss()
                    //on error storing the name to sqlite with status unsynced
                    saveNameToLocalStorage(name, NAME_NOT_SYNCED_WITH_SERVER)
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("name", name)
                return params
            }
        }

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }

    //saving the name to local storage
    private fun saveNameToLocalStorage(name: String, status: Int) {
        editTextName!!.setText("")
        db!!.addName(name, status)
        val n = Model(name, status)
        list!!.add(n)
        refreshList()
    }

    override fun onClick(view: View) {
        saveNameToServer()
    }


}
