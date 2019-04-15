package com.daviapps.numeros.internet;
import java.io.*;
import android.content.*;
import android.app.*;
import android.os.*;
import com.daviapps.numeros.domain.*;
import java.net.*;
import android.widget.*;
import android.net.*;

public class UpdateDownloader extends AsyncTask<ApkVersion, Integer, File> {
	private Context context;
	private ProgressDialog dialog;

	public UpdateDownloader(Context context){
		this.context = context;
		this.dialog = new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute(){
		this.dialog.setCancelable(false);
		this.dialog.setTitle("Baixando");
		this.dialog.setProgressNumberFormat("%1dKB / %2dKB");
		this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		this.dialog.setMax(1);
		this.dialog.show();

		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer...progress){
		this.dialog.setProgress(progress[0]);

		super.onProgressUpdate(progress);
	}

	@Override
	protected File doInBackground(ApkVersion...lastVersion){
		try {
			int download_length_divide = 1024;

			URL url = lastVersion[0].getLink();
			File file = this.context.getExternalFilesDir("update.apk");

			// Network input

			HttpURLConnection ct = (HttpURLConnection) url.openConnection();
			//ct.setRequestMethod("GET");
			//ct.setDoInput(true);
			ct.connect();

			this.dialog.setMax(ct.getContentLength() / download_length_divide);

			// 8k buffer
			InputStream is = new BufferedInputStream(url.openStream(), 8129);

			// File output
			file.getParentFile().mkdirs();

			if(file.exists())
				file.delete();

			OutputStream os = new FileOutputStream(file);
			byte data[] = new byte[1024];
			int count = 0;
			int progress = 0;

			// Write file
			while((count = is.read(data)) != -1){
				progress += count;
				publishProgress(progress / download_length_divide);

				os.write(data, 0, count);

				//try{Thread.sleep(10);} catch(Exception ex){}
			}

			os.flush();

			os.close();
			is.close();

			return file;


		} catch(IOException ex){ return null; }
	}

	@Override
	protected void onPostExecute(final File result){
		this.dialog.dismiss();

		if(result == null){
			this.dialog.dismiss();
			Toast.makeText(context, "Download error", Toast.LENGTH_SHORT).show();
			return;
		}

		new AlertDialog.Builder(this.context)
			.setTitle("Download concluido")
			.setMessage("Agora falta pouco.\nPressione INSTALAR para concluir")
			.setCancelable(false)
			.setPositiveButton("Instalar", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2){
					Intent intent = new Intent(Intent.ACTION_VIEW);
					Uri uri = Uri.fromFile(result);

					intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, uri);
					intent.setDataAndType(uri, "application/vnd.android.package-archive");
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

					context.startActivity(intent);
				}
			})
			.show();


		super.onPostExecute(result);
	}
}
