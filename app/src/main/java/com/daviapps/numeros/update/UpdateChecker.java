package com.daviapps.numeros.update;
import android.content.*;
import android.os.*;
import com.daviapps.numeros.domain.*;
import java.net.*;
import java.util.*;
import org.json.*;
import java.io.*;
import android.app.*;
import android.widget.*;
import com.daviapps.numeros.*;
import com.daviapps.numeros.dialog.*;

public class UpdateChecker extends AsyncTask<String, Integer, ApkVersion> {
	private static int S_ALRIGHT = 1, S_EX_IO = 2, S_EX_JSON;

	private Context context;
	private int status = 0;

	public UpdateChecker(Context context){
		this.context = context;
	}

	@Override
	protected void onPreExecute(){
		//Toast.makeText(this.context, "Buscando atualizações..", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected ApkVersion doInBackground(String..._url){
		ApkVersion version = null;

		try {
			URL url = new URL(_url[0]);
			//Scanner sc = new Scanner(new InputStreamReader(url.openStream())).useDelimiter("\\A");
			Scanner sc = new Scanner(url.openStream()).useDelimiter("\\A");

			JSONObject dApp = new JSONObject(sc.next());

			sc.close();
			
			
			// Load json index	
			JSONArray dVersions = dApp.getJSONObject("platforms").getJSONArray("android");
			JSONObject lastVersion = dVersions.getJSONObject(0);

			version = new ApkVersion();

			version.setVersion(new Version(lastVersion.getJSONArray("version").join(".")));
			version.setReleaseDate(new Date(lastVersion.getJSONArray("release_date").join("/")));
			version.setLink(new URL(lastVersion.getString("link")));
			version.setMinSdkVersion(lastVersion.getInt("minSdkVersion"));

			status = S_ALRIGHT;
			
			//Toast.makeText(context, dApp.toString(), Toast.LENGTH_SHORT).show();
			
			/*for(int i = 0; i < dLinks.length(); i++){
				// Load json index
				JSONObject dApp = dLinks.getJSONObject(i);

				// Check application name
				if(dApp.getString("name").equals(this.context.getString(R.string.app_name))){
					JSONArray dVersions = dApp.getJSONObject("platforms").getJSONArray("android");
					JSONObject lastVersion = dVersions.getJSONObject(0);

					version = new ApkVersion();

					version.setVersion(new Version(lastVersion.getJSONArray("version").join(".")));
					version.setReleaseDate(new Date(lastVersion.getJSONArray("release_date").join("/")));
					version.setLink(new URL(lastVersion.getString("link")));
					version.setMinSdkVersion(lastVersion.getInt("minSdkVersion"));

					status = S_ALRIGHT;

					break;
				}
			}*/

		}
		catch (JSONException ex){ status = S_EX_JSON; }
		catch (IOException ex){ status = S_EX_IO; }

		return version;
	}

	@Override
	protected void onPostExecute(final ApkVersion result){
		try {
			if(status == S_ALRIGHT){
				if(result != null){
					int cmpVersion = result.getVersion().compareTo(new Version(this.context.getString(R.string.app_version)));
					int cmpMinSdk = result.getMinSdkVersion().compareTo(Build.VERSION.SDK_INT);
					
					// Aparelho ultrapassado
					if(cmpMinSdk == 1){
						Toast.makeText(this.context, "Seu aparelho é incompativel com a últuma versão do aplicativo", Toast.LENGTH_LONG).show();
						return;
					}
					
					// Aplicatico desatualizado
					if(cmpVersion == 1){
						//Toast.makeText(this.context, "Nova versão disponível", Toast.LENGTH_SHORT).show();

						new AlertDialog.Builder(this.context)
							.setCancelable(false)
							.setTitle("Nova atualização disponível")
							.setMessage(String.format("Versão: %s\nLançado em: %s", result.getVersion(), result.getReleaseDate().toLocaleString().split(" 00")[0]))
							.setPositiveButton("Atualizar", new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface p1, int p2){
									new UpdateDownloader(UpdateChecker.this.context).execute(result);
								}
							})
							.setNegativeButton("Ignorar", null)
							.show();
					}


				} else
					Toast.makeText(this.context, "Não foi possivel carregar a última versão.", Toast.LENGTH_LONG).show();

				this.cancel(true);
			}
			else
			if(status == S_EX_IO)
				Toast.makeText(this.context, "Não foi possivel buscar novas atualizações.\nVerifique a sua conexão com a internet.", Toast.LENGTH_LONG).show();
			else
			if(status == S_EX_JSON)
				Toast.makeText(this.context, "O index de download está corrompido.", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(this.context, "Aconteceu um erro inesperado ao obter novas atualizações.", Toast.LENGTH_LONG).show();

		}
		catch(Exception ex){
			ErrorDialog.show(this.context, "UpdateChecker.onPostExecute", ex.getClass().getName() + " - " + ex.getMessage());
		}

		super.onPostExecute(result);
	}
	
	// Static method
	public static void check(Context context){
		new UpdateChecker(context).execute(context.getString(R.string.update_index));
	}
}

