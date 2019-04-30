package com.daviapps.numeros.database;
import com.daviapps.numeros.domain.*;
import android.content.*;
import java.util.*;
import android.database.sqlite.*;
import com.daviapps.numeros.dialog.*;
import android.database.*;

public class PlayerDB extends DataSet<Player> {
	private static final String [] columns = new String[]{ 
		"id", "nickname", "password", "maxscore"
	};
	
	private Core core;
	
	public PlayerDB(Context context){
		this.core = new Core(context);
		
		
		if(this.count("nickname = \"anonymous\"") == 0)
			this.insert(new Player("anonymous"));
	}

	@Override
	public void insert(Player item){
		SQLiteDatabase db = core.getWritableDatabase();

		try {
			ContentValues values = new ContentValues();

			values.put(columns[1], item.getNickname());
			values.put(columns[2], item.getPassword());
			values.put(columns[3], item.getMaxScore());
			
            db.insert(Core.DB_NAME, null, values);

        } 
		catch(Exception ex) {
            ErrorDialog.show(core.context, String.format("DB(%s): Insert object", Core.DB_NAME), ex.getMessage());
        }
		finally {
			db.close();
		}
	}

	@Override
	public void update(Player item){
		SQLiteDatabase db = core.getWritableDatabase();

        try {
			ContentValues values = new ContentValues();

			values.put(columns[1], item.getNickname());
			values.put(columns[2], item.getPassword());
			values.put(columns[3], item.getMaxScore());
			
			
            db.update(Core.DB_NAME, values, "id = ?", new String[]{ Integer.toString(item.getId()) });

        }
		catch (Exception ex) {
            ErrorDialog.show(core.context, String.format("DB(%s): Insert object", Core.DB_NAME), ex.getMessage());
        }
		finally {
            db.close();
        }
	}

	@Override
	public void delete(Player item){
		SQLiteDatabase db = core.getWritableDatabase();
        db.delete(Core.DB_NAME, "id = ?", new String[]{ Integer.toString(item.getId()) });
        db.close();
	}

	@Override
	public List<Player> select(String where, String order){
		SQLiteDatabase db = core.getReadableDatabase();
		List<Player> results = new ArrayList<>();

		try {
			Cursor c = db.query(Core.DB_NAME, columns, where, null, null, null, order);

        	while (c.moveToNext()){
				Player item = new Player(c.getInt(0));
				
				item.setNickname(c.getString(1));
				item.setPassword(c.getString(2));
				item.setMaxScore(c.getInt(3));
				
				results.add(item);
			}

			c.close();
		} 
		catch (Exception ex) {
			ErrorDialog.show(core.context, String.format("DB(%s): Select", Core.DB_NAME), ex.getMessage());
		}
		finally {
			db.close();
		}

		return results;
	}
	
	class Core extends SQLiteOpenHelper {
		private static final String DB_NAME = "players";
		private static final int DB_VERSION = 1;
		
		private Context context;
		
		public Core(Context context){
			super(context, DB_NAME, null, DB_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db){
			// TODO: Implement this
			try {
                db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (" +
										 "id INTEGER PRIMARY KEY AUTOINCREMENT," +
										 "nickname STRING UNIQUE NOT NULL," +
										 "password STRING," +
										 "maxscore INTEGER" +
										 ");", DB_NAME));
            } catch (Exception ex){
                ErrorDialog.show(this.context, String.format("DB(%s): Core.onCreate", DB_NAME), ex.getMessage());
            }
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			try {
                db.execSQL(String.format("DROP TABLE IF EXISTS %s", DB_NAME));
                onCreate(db);
            } catch (Exception ex) {
                ErrorDialog.show(this.context, String.format("DB(%s): Core.onUpgrade", DB_NAME), ex.getMessage());
            }
		}
	}
}
