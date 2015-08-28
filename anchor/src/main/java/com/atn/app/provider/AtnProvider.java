package com.atn.app.provider;

import com.atn.app.AtnApp;
import com.atn.app.utils.SharedPrefUtils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class AtnProvider extends ContentProvider {

	// Used for debugging and logging
    private static final String TAG = "AtnProvider";
    /**
     * The database that the provider uses as its underlying data store
     */
    private static final String DATABASE_NAME = "access_the_night.db";

    /**
     * The database version
     */
    private static final int DATABASE_VERSION = 5;//last 4 at 1.3
	// Handle to a new DatabaseHelper.
    private DatabaseHelper mOpenHelper;
	
	
 // The incoming URI matches the FoureSquare URI pattern
    private static final int VANUES = 1;
    // The incoming URI matches the InstagramMedia URI pattern
    private static final int IG_MEDIA = 2;
    
    //
    private static final int FORUM = 3;
    
    //
    private static final int CATEGORY = 5;
    
    private static final int DELETE_ALL = 4;
    
    private static final int REVIEW = 6;
    private static final int COMMENT = 7;

    private static final int REVIEW_LIMIT_2 = 8;
    
    
    /**
     * A UriMatcher instance
     */
    private static final UriMatcher sUriMatcher;
    static {
	        /*
	         * Creates and initializes the URI matcher
	         */
	        // Create a new instance
	        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	        // Add a pattern that routes URIs terminated with "notes" to a NOTES operation
	        sUriMatcher.addURI(Atn.AUTHORITY, "vanues", VANUES);
	        // Add a pattern that routes URIs terminated with "notes" plus an integer
	        // to a note ID operationvanues//vanues
	        sUriMatcher.addURI(Atn.AUTHORITY, "igmedia", IG_MEDIA);
	        
	        sUriMatcher.addURI(Atn.AUTHORITY, "forum", FORUM);
	        
	        sUriMatcher.addURI(Atn.AUTHORITY, "cleandb", DELETE_ALL);
	        
	        sUriMatcher.addURI(Atn.AUTHORITY, "cat", CATEGORY);
	        
	        sUriMatcher.addURI(Atn.AUTHORITY, "review", REVIEW);
	        
	        sUriMatcher.addURI(Atn.AUTHORITY, "comment", COMMENT);
	        
	        sUriMatcher.addURI(Atn.AUTHORITY, "reviewlimit", REVIEW_LIMIT_2);
       }
    
   
   // private static final Object lockObject = new  Object();
    
	@Override
	public boolean onCreate() {
		// Creates a new helper object. Note that the database itself isn't opened until
	       // something tries to access it, and it's only created if it doesn't already exist.
	       mOpenHelper = new DatabaseHelper(getContext());
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
			
		Cursor c;
			SQLiteDatabase db = mOpenHelper.getReadableDatabase();
			/**
			 * Choose the projection and adjust the "where" clause based on URI
			 * pattern-matching.
			 */
			switch (sUriMatcher.match(uri)) {
				// If the incoming URI is for notes, chooses the Notes projection
				case VANUES: {
					if(projection!=null&&projection.length==1) {
                        c = db.query(Atn.Venue.TABLE_NAME, null, selection,
                                selectionArgs, null, null, sortOrder, projection[0]);
                    } else {
                    	c = db.query(Atn.Venue.TABLE_NAME, projection, selection,
                                selectionArgs, null, null, sortOrder);                         
                    }
				}
					break;
				case IG_MEDIA: {
					c = db.query(Atn.InstagramMedia.TABLE_NAME, null, selection,
							selectionArgs, null, null, sortOrder);
				}
					break;
					
				case FORUM: {
					c = db.query(Atn.Forum.TABLE_NAME, null, selection,selectionArgs, null, null, sortOrder);
				}
					break;
				case CATEGORY: {
					c = db.query(Atn.Category.TABLE_NAME, null, selection,selectionArgs, null, null, sortOrder);
				}
					break;
				case REVIEW: {
					c = db.query(Atn.ReviewTable.TABLE_NAME, null, selection,selectionArgs, null, null, sortOrder);
				}
					break;
				case REVIEW_LIMIT_2: {
					c = db.query(Atn.ReviewTable.TABLE_NAME, null, selection,selectionArgs, null, null, sortOrder,String.valueOf(2));
				}
					break;
					
				case COMMENT: {
					c = db.query(Atn.CommentTable.TABLE_NAME, null, selection,selectionArgs, null, null, sortOrder);
				}
					break;
				default:
					// If the URI doesn't match any of the known patterns, throw an
					// exception.
					throw new IllegalArgumentException("Unknown URI " + uri);
			}
			// Tells the Cursor what URI to watch, so it knows when its source data
			// changes
				c.setNotificationUri(getContext().getContentResolver(), uri);
			return c;
		
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
	
		
			// Opens the database object in "write" mode.
	        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	        long rowId = -1;
	        Uri noteUri;
	     // Does the delete based on the incoming URI pattern.
	        switch (sUriMatcher.match(uri)) {
	            case VANUES:
				rowId = db.insert(Atn.Venue.TABLE_NAME, null,initialValues);
				noteUri = ContentUris.withAppendedId(Atn.Venue.CONTENT_ID_URI_BASE, rowId);
	                break;
	            case IG_MEDIA:
	            	rowId = db.insert(Atn.InstagramMedia.TABLE_NAME, null, initialValues);
	            	noteUri = ContentUris.withAppendedId(
	    					Atn.InstagramMedia.CONTENT_ID_URI_BASE, rowId);
	                break;
	                
	            case FORUM:
	            	rowId = db.insert(Atn.Forum.TABLE_NAME, null, initialValues);
	            	noteUri = ContentUris.withAppendedId(
	    					Atn.Forum.CONTENT_ID_URI_BASE, rowId);
	                break;
	            case CATEGORY:
	            	rowId = db.insert(Atn.Category.TABLE_NAME, null, initialValues);
	            	noteUri = ContentUris.withAppendedId(Atn.Category.CONTENT_ID_URI_BASE, rowId);
	                break;
	                
	            case REVIEW:
	            	rowId = db.insert(Atn.ReviewTable.TABLE_NAME, null, initialValues);
	            	noteUri = ContentUris.withAppendedId(
	    					Atn.ReviewTable.CONTENT_ID_URI_BASE, rowId);
	                break;
	            case COMMENT:
	            	rowId = db.insert(Atn.CommentTable.TABLE_NAME, null, initialValues);
	            	noteUri = ContentUris.withAppendedId(Atn.ReviewTable.CONTENT_ID_URI_BASE, rowId);
	                break;
	                
	            // If the incoming pattern is invalid, throws an exception.
	            default:
	                throw new IllegalArgumentException("Unknown URI " + uri);
	        }

	        // If the insert succeeded, the row ID exists.
	        if (rowId > 0) {
	            // Creates a URI with the note ID pattern and the new row ID appended to it.
	             // Notifies observers registered against this provider that the data changed.
	            getContext().getContentResolver().notifyChange(noteUri, null);
	            return noteUri;
	        }
	        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
	       // throw new SQLException("Failed to insert row into " + uri);
		  return noteUri;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		
		 // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        // Does the delete based on the incoming URI pattern.
        switch (sUriMatcher.match(uri)) {
            case VANUES:
			count = db.delete(Atn.Venue.TABLE_NAME, selection, selectionArgs);
                break;

            case IG_MEDIA:
			count = db.delete(Atn.InstagramMedia.TABLE_NAME, selection,
					selectionArgs);
                break;

            case FORUM:
            	count = db.delete(Atn.Forum.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORY:
            	count = db.delete(Atn.Category.TABLE_NAME, selection, selectionArgs);
                break;
                
            case REVIEW:
            	count = db.delete(Atn.ReviewTable.TABLE_NAME, selection, selectionArgs);
                break;
            case COMMENT:
            	count = db.delete(Atn.CommentTable.TABLE_NAME, selection, selectionArgs);
                break;
                
            case DELETE_ALL: {
			count = db.delete(Atn.Venue.TABLE_NAME, selection, selectionArgs);
            }
                break;
                
            // If the incoming pattern is invalid, throws an exception.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Returns the number of rows deleted.
        return count;
		//}
	}

	@SuppressWarnings("deprecation")
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		 // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        // Does the delete based on the incoming URI pattern.
        switch (sUriMatcher.match(uri)) {
            case VANUES:
			count = db.update(Atn.Venue.TABLE_NAME, values,selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
                break;
            case IG_MEDIA:
            	count = db.update(Atn.InstagramMedia.TABLE_NAME, values,selection, selectionArgs);
                break;
            case FORUM:{
            	count = db.update(Atn.Forum.TABLE_NAME, values,selection, selectionArgs);
            	 getContext().getContentResolver().notifyChange(uri, null);
            	 break;
            }
            case CATEGORY:
            	count = db.update(Atn.Category.TABLE_NAME, values,selection, selectionArgs);
                break;
                
            case REVIEW:
            	count = db.update(Atn.ReviewTable.TABLE_NAME, values,selection, selectionArgs);
                break;
            case COMMENT:
            	count = db.update(Atn.CommentTable.TABLE_NAME, values,selection, selectionArgs);
                break;
            // If the incoming pattern is invalid, throws an exception.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
       
        // Returns the number of rows deleted.
        return count;
		//}
	}

	
	/**
    *
    * This class helps open, create, and upgrade the database file. Set to package visibility
    * for testing purposes.
    */
   static class DatabaseHelper extends SQLiteOpenHelper {
	   	   DatabaseHelper(Context context) {
           super(context, DATABASE_NAME, null, DATABASE_VERSION);
       }

       /**
        *
        * Creates the underlying database with table name and column names taken from the
        *  class.
        */
       @SuppressWarnings("deprecation")
	@Override
       public void onCreate(SQLiteDatabase db) {

           db.execSQL(Atn.Venue.create_table_query);
           db.execSQL(Atn.InstagramMedia.create_table_query);
           db.execSQL(Atn.Forum.create_table_query);
           db.execSQL(Atn.Category.create_table_query);
           db.execSQL(Atn.ReviewTable.create_table_query);
           db.execSQL(Atn.CommentTable.create_table_query);
       }

       /**
        *
        * Demonstrates that the provider must consider what happens when the
        * underlying datastore is changed. In this sample, the database is upgraded the database
        * by destroying the existing data.
        * A real application should upgrade the database in place.
        */
       @SuppressWarnings("deprecation")
	   @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

           // Logs that the database is being upgraded
           Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                   + newVersion + ", which will destroy all old data");

           // Kills the table and existing data
           db.execSQL("DROP TABLE IF EXISTS "+Atn.Venue.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS "+Atn.InstagramMedia.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS "+Atn.Forum.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS "+Atn.Category.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS "+Atn.ReviewTable.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS "+Atn.CommentTable.TABLE_NAME);
           
           // Recreates the database with a new version
           onCreate(db);
           SharedPrefUtils.clearAll(AtnApp.getAppContext());
       }
   }

   DatabaseHelper getOpenHelperForTest() {
       return mOpenHelper;
   }

}
