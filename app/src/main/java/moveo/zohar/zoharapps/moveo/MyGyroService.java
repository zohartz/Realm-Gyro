package moveo.zohar.zoharapps.moveo;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;



public class MyGyroService extends Service {
    private Realm myRealm;
    private int Max=0;
    private RealmResults<Record> results;
    private SensorManager sensorManager = null;
    private Sensor gyroscopeSensor=null;
    private RealmConfiguration realmConfiguration;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       // RealmConfiguration & SensorManager
        configuration();

        SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                // get get size of the records make sure its not over 500 each time
                RealmResults<Record> res=myRealm.where(Record.class).findAll();
                Log.d("size",String.valueOf(res.size()));
                if(res.size()<=500) {
                    if (sensorEvent.values[2] > 0.5f ) { // z axis - screen facing to user and move anticlockwise
                        addDataToRealm("anticlockwise");
                    } else if (sensorEvent.values[2] < -0.5f) { // z axis - screen facing to user and move clockwise
                        addDataToRealm("clockwise");
                      }
                }

                if(res.size()>0) {
                    //if there is a record send message to main activity for action
                    sendMessage();
                }
            }

            // Send an Intent with an action named "custom-event-name". The Intent
            // sent should
            // be received by the ReceiverActivity.
            private void sendMessage() {
                Log.d("sender", "Broadcasting message");
                Intent intent = new Intent("clockwise-rotation");

                RealmQuery<Record> query = myRealm.where(Record.class);
                final Record record = query.equalTo("id",Max).findFirst(); //pull aut the last record

                if(record!=null) {
                    String action = record.getAction();
                    intent.putExtra("message", action);
                    deleteFromRealm(record);
                }
                else{
                    intent.putExtra("message", "non");

                }
                LocalBroadcastManager.getInstance(MyGyroService.this).sendBroadcast(intent);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

// Register the listener
        sensorManager.registerListener(gyroscopeSensorListener,
                gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL );

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void addDataToRealm(String action) {

        int id = -1;
        results = myRealm.where(Record.class).findAll();

        if(results.size()  == 0) {
            id = 1;
        } else {
            id= results.max("id").intValue() + 1;
            Max=id;

        }

        myRealm.beginTransaction();
        Record recordDetailsModel = myRealm.createObject(Record.class);
        recordDetailsModel.setId(id);
        recordDetailsModel.setAction(action);
        myRealm.commitTransaction();
    }



    public void configuration(){
        realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        if(myRealm!=null) {
            Realm.deleteRealm(realmConfiguration);
        }
        myRealm = Realm.getInstance(MyGyroService.this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }


public void deleteFromRealm(Record record) {
         if (!myRealm.isInTransaction()) {
              myRealm.beginTransaction();
          }
      record.removeFromRealm();
      myRealm.commitTransaction();
    //check the record was remved
    RealmResults<Record> res2 = myRealm.where(Record.class).findAll();
    Log.d("size now", String.valueOf(res2.size()));
    }



}
