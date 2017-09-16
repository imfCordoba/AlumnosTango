package com.madrefoca.alumnostango.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeType;

import java.sql.SQLException;

/**
 * Created by fernando on 05/09/17.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "AlumnosTango.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Attendee, Integer> attendeeDao;
    private Dao<AttendeeType, Integer> attendeeTypesDao;
    //lo mismo para cada clase/tabla del modelo

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * What to do when your database needs to be created. Usually this entails creating the tables and loading any
     * initial data.
     * <p>
     * <p>
     * <b>NOTE:</b> You should use the connectionSource argument that is passed into this method call or the one
     * returned by getConnectionSource(). If you use your own, a recursive call or other unexpected results may result.
     * </p>
     *
     * @param sqliteDatabase         Database being created.
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Attendee.class);
            TableUtils.createTable(connectionSource, AttendeeType.class);
            // TODO: 8/19/2017 lo mismo para los dao de las otras clases
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create databases", e);
        }
    }

    /**
     * What to do when your database needs to be updated. This could mean careful migration of old data to new data.
     * Maybe adding or deleting database columns, etc..
     * <p>
     * <p>
     * <b>NOTE:</b> You should use the connectionSource argument that is passed into this method call or the one
     * returned by getConnectionSource(). If you use your own, a recursive call or other unexpected results may result.
     * </p>
     *
     * @param sqliteDatabase         Database being upgraded.
     * @param connectionSource To use get connections to the database to be updated.
     * @param oldVersion       The version of the current database so we can know what to do to the database.
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, AttendeeType.class, true);
            TableUtils.dropTable(connectionSource, Attendee.class, true);

            // TODO: 8/19/2017 lo mismo para los dao de las otras clases

            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVersion + " to new "
                    + newVersion, e);
        }
    }

    public void clearTables() {
        try {
            TableUtils.clearTable(connectionSource, AttendeeType.class);
            TableUtils.clearTable(connectionSource, Attendee.class);
            // TODO: 8/19/2017 lo mismo para los dao de las otras clases
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to clear tables", e);
        }
    }

    public Dao<AttendeeType, Integer> getAttendeeTypeDao() throws SQLException {
        if (attendeeTypesDao == null) {
            attendeeTypesDao = getDao(AttendeeType.class);
        }
        return attendeeTypesDao;
    }

    public Dao<Attendee, Integer> getAttendeeDao() throws SQLException {
        if (attendeeDao == null) {
            attendeeDao = getDao(Attendee.class);
        }
        return attendeeDao;
    }
    //// TODO: 8/19/2017  el mismo metodo get para cada clase/tabla del modelo
}

