package com.example.subscriber.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.subscriber.models.Invoice;
import com.example.subscriber.models.Message;
import com.example.subscriber.models.Payment;
import com.example.subscriber.models.Subscriber;
import com.example.subscriber.models.User;

@Database(
    entities = {Subscriber.class, Invoice.class, Payment.class, User.class, Message.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SubscriberDao subscriberDao();
    public abstract InvoiceDao invoiceDao();
    public abstract PaymentDao paymentDao();
    public abstract UserDao userDao();
    public abstract MessageDao messageDao();
    
    private static volatile AppDatabase INSTANCE;
    
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "subscriber_management_db"
                    )
                    .allowMainThreadQueries()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
