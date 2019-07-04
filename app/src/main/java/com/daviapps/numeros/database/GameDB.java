package com.daviapps.numeros.database;
import com.daviapps.numeros.domain.*;
import android.content.*;
import java.util.*;
import android.database.sqlite.*;
import com.daviapps.numeros.dialog.*;
import android.database.*;

public class GameDB extends DataSet<Game> {
	private static final String [] columns = new String[]{ 
			"id", "player", "status", "level", "hits", "faults", "score", "time", "incr", "decr" , "dstart", "dlast"
	};
	
	private Core core;
	
	public GameDB(Context context){
		this.core = new Core(context);
	}

	@Override
	public Game insert(Game item){
		SQLiteDatabase db = core.getWritableDatabase();
		
		try {
			ContentValues values = new ContentValues();
			
			values.put(columns[1], item.getPlayer());
			values.put(columns[2], item.getStatus());
			values.put(columns[3], item.getLevel());
			values.put(columns[4], item.getHits());
			values.put(columns[5], item.getFaults());
			values.put(columns[6], item.getScore());
			values.put(columns[7], item.getTime());
			values.put(columns[8], item.getIncrement());
			values.put(columns[9], item.getDecrement());
			
			//if(item.getDateStart() != null)
				values.put(columns[10], timeFormat.format(item.getDateStart()));
			
			//if(item.getDateLast() != null)
				values.put(columns[11], timeFormat.format(item.getDateLast()));
			
            db.insert(Core.DB_NAME, null, values);
			
			// Return inserted item with id
			List<Game> s = select("1 = 1");
			
			return s.get(s.size() -1);
			
        } 
		catch(Exception ex) {
            ErrorDialog.show(core.context, String.format("DB(%s): Insert object", Core.DB_NAME), ex.getMessage());
        }
		finally {
			db.close();
		}
		
		return item;
	}

	@Override
	public void update(Game item){
		SQLiteDatabase db = core.getWritableDatabase();
		
        try {
			ContentValues values = new ContentValues();
			
			values.put(columns[1], item.getPlayer());
			values.put(columns[2], item.getStatus());
			values.put(columns[3], item.getLevel());
			values.put(columns[4], item.getHits());
			values.put(columns[5], item.getFaults());
			values.put(columns[6], item.getScore());
			values.put(columns[7], item.getTime());
			values.put(columns[8], item.getIncrement());
			values.put(columns[9], item.getDecrement());
			
			//if(item.getDateStart() != null)
				values.put(columns[10], timeFormat.format(item.getDateStart()));

			//if(item.getDateLast() != null)
				values.put(columns[11], timeFormat.format(item.getDateLast()));
			
            db.update(Core.DB_NAME, values, "id = ?", new String[]{ Integer.toString(item.getId()) });
			
        }
		catch (Exception ex) {
            ErrorDialog.show(core.context, String.format("DB(%s): Update object", Core.DB_NAME), ex.getMessage());
        }
		finally {
            db.close();
        }
	}

	@Override
	public void delete(Game item){
		SQLiteDatabase db = core.getWritableDatabase();
        db.delete(Core.DB_NAME, "id = ?", new String[]{ Integer.toString(item.getId()) });
        db.close();
	}

	@Override
	public List<Game> select(String where, String order){
		SQLiteDatabase db = core.getReadableDatabase();
		List<Game> results = new ArrayList<>();

		try {
			Cursor c = db.query(Core.DB_NAME, columns, where, null, null, null, order);
			
        	while (c.moveToNext()){
				
				results.add(new Game.Builder()
						.setId(c.getInt(0))
						.setPlayer(c.getInt(1))
						.setStatus(c.getInt(2))
						.setLevel(c.getInt(3))
						.setHits(c.getInt(4))
						.setFaults(c.getInt(5))
						.setScore(c.getInt(6))
						.setTime(c.getInt(7))
						.setIncrement(c.getInt(8))
						.setDecrement(c.getInt(9))
						.setDateStart(timeFormat.parse(c.getString(10)))
						.setDateLast(timeFormat.parse(c.getString(11)))
						.build()
				);
				
        
				
                /*NumberDrawn item = new NumberDrawn(c.getInt(0));

                item.setRaffle(c.getInt(c.getColumnIndex(columns[1])));
                item.setValues(c.getString(c.getColumnIndex(columns[2])));
                item.setDate(timeFormat.parse(c.getString(c.getColumnIndex(columns[3]))));

                items.add(item);*/
			}
			
			c.close();
		} 
		catch (Exception ex) {
			ErrorDialog.show(core.context, String.format("DB(%s): Select - %s", Core.DB_NAME, ex.getClass().getName()), ex.getMessage());
		}
		finally {
			db.close();
		}
		
		return results;
	}
	
	class Core extends SQLiteOpenHelper {
		// id, player, status, level, hits, faults, score, time, incr, decr, dstart, dlast
		
		private static final String DB_NAME = "games";
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
										 "player INTEGER NOT NULL," +
										 "status INTEGER NOT NULL," +
										 "level INTEGER NOT NULL," +
										 "hits INTEGER NOT NULL," +
										 "faults INTEGER NOT NULL," +
										 "score INTEGER NOT NULL," +
										 "time INTEGER NOT NULL," +
										 "incr INTEGER NOT NULL," +
										 "decr INTEGER NOT NULL," +
										 "dstart DATETIME DEFAULT current_timestamp," +
										 "dlast DATETIME DEFAULT current_timestamp" +
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
