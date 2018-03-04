package karun.com.online_sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class NetworkStateChecker : BroadcastReceiver() {


    private var context: Context? = null
    private var db: DatabaseHelper? = null


    override fun onReceive(context: Context, intent: Intent) {

        this.context = context
        db = DatabaseHelper(context)
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI || activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                val cursor = db!!.unsyncedNames
                if (cursor.moveToFirst()) {
                    do {
                        saveName(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME))
                        )
                    } while (cursor.moveToNext())
                }
            }
        }
    }

    private fun saveName(id: Int, name: String) {


        val stringRequest = object : StringRequest(com.android.volley.Request.Method.POST, MainActivity.url,
                com.android.volley.Response.Listener { response ->
                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {

                            db!!.updateNameStatus(id, MainActivity.NAME_SYNCED_WITH_SERVER)
                            context!!.sendBroadcast(Intent(MainActivity.DATA_SAVED_BROADCAST))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener {}) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("name", name)
                return params
            }
        }

        VolleySingleton.getInstance(context!!).addToRequestQueue(stringRequest)
    }
}
