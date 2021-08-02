package com.liucj.libnetwork.cache;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.liucj.libcommon.AppGlobals;

import org.jetbrains.annotations.NotNull;

@Database(entities = {Cache.class}, version = 1, exportSchema = true)
public abstract class CacheDatabase extends RoomDatabase {
    private static final CacheDatabase database;

    static {
        //创建一个内存数据库
        //但是这种数据库的数据只存在于内存中，也就是进程被杀，数据随之丢失
        // Room.inMemoryDatabaseBuilder
        database = Room.databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, "ppjoke_cache")
                //是否允许在主线程进行查询
                .allowMainThreadQueries()
//                //数据库创建和打开后回调
//                .addCallback()
//                //设置查询线程池
//                .setQueryExecutor().openHelperFactory()
//                //room的日志模式
//                .setJournalMode()
//                //数据库升级异常之后的回滚
//                .fallbackToDestructiveMigration()
//                //数据库升级异常之后根据指定版本进行回滚
//                .fallbackToDestructiveMigrationFrom()
//                .addMigrations(CacheDatabase.mMigration)
                .build();
    }

    public abstract CacheDao getCache();

    public static CacheDatabase get() {
        return database;
    }

    static Migration mMigration = new Migration(1, 3) {
        @Override
        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
            database.execSQL("alter table teacher rename to student");
        }
    };
}
