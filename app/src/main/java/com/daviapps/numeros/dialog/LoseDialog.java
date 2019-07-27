package com.daviapps.numeros.dialog;
import android.app.*;
import android.os.*;
import android.content.*;
import com.daviapps.numeros.*;
import com.daviapps.numeros.domain.*;
import android.widget.*;
import android.view.*;

public class LoseDialog extends Dialog {
	// Layout
	private TextView tvScore, tvHits, tvFaults, tvAverage;
	private Button btTryAgain;
	
	// Data
	private Game game;
	
	// Listener
	private OnTryAgainListener onTryAgainListener;
	
	public LoseDialog(Context context, Game game){
		super(context);
		this.game = game;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_lose);
		this.setCanceledOnTouchOutside(false);
		this.setTitle("Você perdeu!");
		
		this.tvScore = findViewById(R.id.dialog_lose_score);
		this.tvHits = findViewById(R.id.dialog_lose_hits);
		this.tvFaults = findViewById(R.id.dialog_lose_faults);
		this.tvAverage = findViewById(R.id.dialog_lose_average);
		this.btTryAgain = findViewById(R.id.dialog_lose_try_again);
		
		// Fill data
		this.tvScore.setText(String.format("%s", game.getScore()));
		this.tvHits.setText(String.format("%s", game.getHits()));
		this.tvFaults.setText(String.format("%s", game.getFaults()));
		this.tvAverage.setText(String.format("%s%%", (((int) (((double) game.getHits() / (game.getHits() + game.getFaults())) * 100)))));
		//this.tvAverage.setText(String.format("%s", g
		
		// onClick Listener
		this.btTryAgain.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if(onTryAgainListener != null){
					onTryAgainListener.onTryAgain();
					dismiss();
				}
			}
		});
		
	}

	@Override
	protected void onStart(){
		super.onStart();
	}
	
	public void setOnTryAgain(OnTryAgainListener listener){
		this.onTryAgainListener = listener;
	}
	
	public interface OnTryAgainListener {
		void onTryAgain();
	}
	
	public static final class Builder extends DialogBuilder<LoseDialog> {
		
		public Builder(Context context, Game game){
			super(new LoseDialog(context, game));
		}

		public Builder setOnTryAgainListener(OnTryAgainListener listener){
			this.dialog.setOnTryAgain(listener);
			return this;
		}
	}
}
